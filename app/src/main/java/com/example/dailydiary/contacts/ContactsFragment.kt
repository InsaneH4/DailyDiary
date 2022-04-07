package com.example.dailydiary.contacts

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.dailydiary.other.MainActivity
import com.example.dailydiary.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ContactsFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val contactsFab = view.findViewById<FloatingActionButton>(R.id.contactsFab)
        val mainActivityView = activity as MainActivity
        contactsFab?.setOnClickListener {
            mainActivityView.makeCurrentFragment(SingleContactFragment())
        }
    }
}