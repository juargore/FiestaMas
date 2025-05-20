package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.runtime.Composable
import com.universal.fiestamas.domain.models.Address
import com.universal.fiestamas.presentation.screens.location.AddressAutoCompleteScreen

@Composable
fun AddressAutoCompleteDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onAddressSelected: (Address?) -> Unit
) {
    if (isVisible) {
        BaseDialog(
            addCloseIcon = false,
            onDismiss = onDismiss,
            content = {
                AddressAutoCompleteScreen(
                    searchForCities = true,
                    onAddressSelected = { address: Address?, _ ->
                        onAddressSelected(address)
                    }
                )
            }
        )
    }
}
