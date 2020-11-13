package com.example.artclass.fragments

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.artclass.databinding.FragmentPhotosBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

// the photos screen
class PhotosFragment : Fragment() {

    // lets make the fragment features
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // the binding of the home fragment
        val binding = FragmentPhotosBinding.inflate(layoutInflater)



        // return the important binding info
        return binding.root
    }
}