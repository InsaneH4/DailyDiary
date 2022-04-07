package com.example.dailydiary.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.dailydiary.other.MainActivity
import com.example.dailydiary.R

class SingleContactFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val backButton = view.findViewById<Button>(R.id.backButton)
        val mainActivityView = activity as MainActivity
        backButton?.setOnClickListener {
            mainActivityView.makeCurrentFragment(ContactsFragment())
        }
    }
}