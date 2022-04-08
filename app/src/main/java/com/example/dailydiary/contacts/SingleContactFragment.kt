package com.example.dailydiary.contacts

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.dailydiary.R
import com.example.dailydiary.other.MainActivity
import kotlinx.android.synthetic.main.fragment_single_contact.*

class SingleContactFragment : Fragment() {
    private val getResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            ContactPhoto.setImageURI(it.data?.data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_single_contact, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mainActivityView = activity as MainActivity
        CancelButton?.setOnClickListener {
            mainActivityView.makeCurrentFragment(ContactsFragment())
        }
        SaveButton?.setOnClickListener {
            mainActivityView.makeCurrentFragment(ContactsFragment())
        }
        ChooseImageBtn?.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            getResult.launch(intent)
        }
    }
}