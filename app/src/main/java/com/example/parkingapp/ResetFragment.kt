package com.example.parkingapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth




class ResetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reset, container, false)

        val buttonBack = view.findViewById<Button>(R.id.to_back_button)
        buttonBack.setOnClickListener {
            findNavController().navigate(R.id.action_resetFragment_to_loginFragment)
        }

        val buttonReset = view.findViewById<Button>(R.id.reset_button)
        buttonReset.setOnClickListener {
            val mAuth = FirebaseAuth.getInstance()
            val emailField = view.findViewById<EditText>(R.id.email_field)
            val email = emailField.text.toString()
            mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext(), "Лист з відновленням паролю, успішно надісланий.", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_resetFragment_to_loginFragment)
                    } else {
                        Toast.makeText(requireContext(), "Помилка! Перевірте дані.", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        return view
    }
}