package com.companyvihva.vihva.Inicio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.companyvihva.vihva.R

class EventDialogFragment : DialogFragment() {

    interface OnEventSaveListener {
        fun onEventSave(event: String)
    }

    private var listener: OnEventSaveListener? = null

    fun setOnEventSaveListener(listener: OnEventSaveListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_evento, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val eventEditText = view.findViewById<EditText>(R.id.eventEditText)
        val saveButton = view.findViewById<Button>(R.id.saveButton)
        val datePicker = view.findViewById<DatePicker>(R.id.datePicker)

        saveButton.setOnClickListener {
            val event = eventEditText.text.toString()
            listener?.onEventSave(event)
            dismiss()
        }
    }
}
