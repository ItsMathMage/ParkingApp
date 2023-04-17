package com.example.parkingapp

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterFragment : Fragment() {

    private lateinit var auth: FirebaseAuth


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        val button = view.findViewById<Button>(R.id.to_login_button)
        button.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        val buttonRegister = view.findViewById<Button>(R.id.register_button)
        buttonRegister.setOnClickListener {
            auth = FirebaseAuth.getInstance()

            val firstnameField = view.findViewById<EditText>(R.id.firstname_field)
            val lastnameField = view.findViewById<EditText>(R.id.lastname_field)
            val emailField = view.findViewById<EditText>(R.id.email_field)
            val phoneField = view.findViewById<EditText>(R.id.phone_field)
            val passwordField = view.findViewById<EditText>(R.id.password_field)
            val passwordCheckField = view.findViewById<EditText>(R.id.password_check_field)

            val firstname = firstnameField.text.toString()
            val lastname = lastnameField.text.toString()
            val email = emailField.text.toString()
            val phone = phoneField.text.toString()
            val password = passwordField.text.toString()
            val passcheck = passwordCheckField.text.toString()


            // Перевірте, чи введено email та пароль.
            if (firstname.isEmpty() || lastname.isEmpty() || email.isEmpty() ||
                phone.isEmpty() || password.isEmpty() || passcheck.isEmpty()) {
                Toast.makeText(requireContext(), "Введіть дані для реєстрації.", Toast.LENGTH_SHORT).show()
            } else if (password != passcheck) {
                Toast.makeText(requireContext(), "Паролі не співпадають", Toast.LENGTH_SHORT).show()
            } else {
                // Створіть нового користувача з допомогою електронної пошти та пароля.
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(requireActivity()) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            user?.sendEmailVerification()
                                ?.addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        Toast.makeText(requireContext(), "Реєстрація успішна. Підтвердження електронної пошти відправлено.", Toast.LENGTH_SHORT).show()

                                        val uid = user.uid
                                        val tempUser = User(uid, firstname, lastname, email, phone, password)
                                        val car = Car("", "", "", "")

                                        val databaseRef = FirebaseDatabase.getInstance().reference
                                        databaseRef.child("users").child(uid).setValue(tempUser)
                                        databaseRef.child("cars").child(uid).setValue(car)

                                        val uidPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                                        val editor = uidPref.edit()
                                        editor.putString("UID", uid)
                                        editor.apply()

                                        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                                    }
                                }
                        } else {
                            // Не вдалося зареєструватися. Показати помилку.
                            Toast.makeText(requireContext(), "Помилка реєстрації. Спробуйте ще раз.", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        return view
    }
}