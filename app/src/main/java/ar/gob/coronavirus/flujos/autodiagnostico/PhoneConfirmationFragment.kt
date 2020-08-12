package ar.gob.coronavirus.flujos.autodiagnostico

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import ar.gob.coronavirus.R
import ar.gob.coronavirus.flujos.identificacion.IdentificacionTelefonoFragment.PREFIJO_TELEFONO
import ar.gob.coronavirus.utils.PhoneUtils
import kotlinx.android.synthetic.main.fragment_phone_confirmation.*

class PhoneConfirmationFragment : Fragment(R.layout.fragment_phone_confirmation) {

    private val activityViewModel by activityViewModels<AutodiagnosticoViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activityViewModel.userInformation.observe(viewLifecycleOwner) {
            val phone = it.phone?.replace(PREFIJO_TELEFONO, "")
            current_phone_text.text = getString(R.string.phone_confirmation_current, phone)
            new_phone_input.setText(phone)
        }
        new_phone_input.doAfterTextChanged {
            if (PhoneUtils.isValidPhone(it?.toString())) {
                continue_button.isEnabled = true
                new_phone_input_layout.error = null
            } else {
                new_phone_input_layout.error = getString(R.string.invalid_phone_error)
                continue_button.isEnabled = false
            }
        }
        continue_button.setOnClickListener {
            activityViewModel.updatePhone("${PREFIJO_TELEFONO}${new_phone_input.text}")
        }
    }
}