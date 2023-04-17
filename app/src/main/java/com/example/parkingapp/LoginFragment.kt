package com.example.parkingapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth


class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        var userPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        var login = userPref.getString("ULOGIN", "")
        var pass = userPref.getString("UPASS", "")
        var isLogin = userPref.getBoolean("ISLOG", false)

        auth = FirebaseAuth.getInstance()


        if (isLogin) {
            auth.signInWithEmailAndPassword(login!!, pass!!)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser!!
                        val uid = user.uid

                        if (user.isEmailVerified) {
                            Toast.makeText(requireContext(), "Успішний вхід в систему", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        } else {
                            Toast.makeText(requireContext(), "Пройдіть валідацію через пошту", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Якщо невдалося, виводимо повідомлення про помилку
                        Toast.makeText(requireContext(), "Невдалося увійти в систему", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val buttonLogin = view.findViewById<Button>(R.id.login_button)
        buttonLogin.setOnClickListener {
            val emailField = view.findViewById<EditText>(R.id.email_field)
            val passwordField = view.findViewById<EditText>(R.id.password_field)

            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(requireContext(), "Будь ласка, заповніть всі поля", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Авторизація за допомогою Firebase Auth
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser!!
                        val uid = user.uid

                        if (user.isEmailVerified) {
                            val uidPref = requireActivity().getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                            val editor = uidPref.edit()
                            editor.putBoolean("ISLOG", true)
                            editor.putString("UID", uid)
                            editor.putString("UPASS", password)
                            editor.putString("ULOGIN", email)
                            editor.apply()

                            Toast.makeText(requireContext(), "Успішний вхід в систему", Toast.LENGTH_SHORT).show()

                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                        } else {
                            Toast.makeText(requireContext(), "Пройдіть валідацію через пошту", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Якщо невдалося, виводимо повідомлення про помилку
                        Toast.makeText(requireContext(), "Невдалося увійти в систему", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        val buttonRegister = view.findViewById<Button>(R.id.to_register_button)
        buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        val resetPassword = view.findViewById<TextView>(R.id.reset_password)
        resetPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetFragment)
        }

        return view
    }
}