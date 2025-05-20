package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.R
import com.universal.fiestamas.domain.models.QuoteProductsInformation
import com.universal.fiestamas.domain.models.response.GetQuoteResponse
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.RoundedEdittext
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.TextRegular
import com.universal.fiestamas.presentation.ui.ValidationText
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable
import com.universal.fiestamas.presentation.ui.cards.CardProductInformation
import com.universal.fiestamas.presentation.utils.extensions.autoSize
import com.universal.fiestamas.presentation.utils.extensions.isRunningOnTablet
import com.universal.fiestamas.presentation.utils.showToast

@Composable
fun NewQuoteOrEditDialog(
    isVisible: Boolean,
    isCancelable: Boolean = true,
    isEditingData: Boolean,
    quote: GetQuoteResponse?,
    editableList: List<QuoteProductsInformation>?,
    onSendNewQuoteClicked: (
        items: List<QuoteProductsInformation>,
        notes: String,
        total: Int
    ) -> Unit,
    onEditQuoteClicked: (
        newItems: List<QuoteProductsInformation>,
        oldSizeItems: Int,
        providerNotes: String,
        personalNotesClient: String,
        personalNotesProvider: String,
        total: Int
    ) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        val context = LocalContext.current
        val originalItemsSize = quote?.elements?.size ?: 0
        var importantNotes by rememberSaveable { mutableStateOf(quote?.notes ?: "") }
        var showQuoteChildDialog by remember { mutableStateOf(false) }
        var isEditingItem by remember { mutableStateOf(false) }
        var showDeleteDialog by remember { mutableStateOf(false) }
        var itemToDelete: QuoteProductsInformation? by remember { mutableStateOf(null) }
        var indexOfEditingItem by remember { mutableIntStateOf(-1) }
        var itemToEdit by remember { mutableStateOf<QuoteProductsInformation?>(null) }
        val items = remember { mutableStateListOf<QuoteProductsInformation>() }
        var total by remember { mutableDoubleStateOf(editableList?.sumOf { it.subtotal.toDouble() } ?: 0.0) }

        LaunchedEffect(editableList) {
            items.clear()
            editableList?.let { items.addAll(it) }
        }

        YesNoDialog(
            isVisible = showDeleteDialog,
            icon = R.drawable.ic_question_circled,
            message = "¿Confirma que desea eliminar el producto seleccionado?",
            onDismiss = { showDeleteDialog = false },
            onOk = {
                showDeleteDialog = false
                itemToDelete?.let { item ->
                    items.remove(item)
                    itemToDelete = null
                    total = items.sumOf { it.subtotal.toDouble() }
                }
            }
        )

        BaseDialog(
            isCancelable = isCancelable,
            onDismiss = onDismiss,
            content = {
                if (showQuoteChildDialog) {
                    ChildQuoteDialog(
                        onDismiss = { showQuoteChildDialog = false },
                        isEditing = isEditingItem,
                        itemToEdit = itemToEdit,
                        onSaved = { qty, desc, price ->
                            if (isEditingItem && indexOfEditingItem != -1) {
                                items.removeAt(indexOfEditingItem)
                            }
                            val subtotal = (qty.toInt() * price.toDouble()).toString()
                            if (isEditingItem) {
                                items.add(indexOfEditingItem, QuoteProductsInformation(qty, desc, price, subtotal))
                            } else {
                                items.add(QuoteProductsInformation(qty, desc, price, subtotal))
                            }
                            total = items.sumOf { it.subtotal.toDouble() }
                            showQuoteChildDialog = false
                            itemToEdit = null
                        }
                    )
                }

                TextMedium(
                    text = if (isEditingData) "Editar Cotización" else "Nueva Cotización",
                    size = 18.sp.autoSize()
                )

                VerticalSpacer(height = 10.dp)
                HorizontalLine(color = Color.Gray, thick = 0.5.dp)
                VerticalSpacer(height = 15.dp)

                val surfaceHeight = if (isRunningOnTablet()) 300.dp else 120.dp
                Box(modifier = Modifier.height(surfaceHeight)) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        CardProductInformation(
                            isTitle = true,
                            addRemoveButton = true,
                            addEditButton = true,
                            item = QuoteProductsInformation(
                                quantity = "Cant.",
                                description = "Descripción",
                                price = "Precio",
                                subtotal = "Subtotal"
                            )
                        )
                        LazyColumn {
                            itemsIndexed(items) {i, it ->
                                CardProductInformation(
                                    item = it,
                                    addRemoveButton = true,
                                    addEditButton = true,
                                    onRemoveClicked = {
                                        itemToDelete = it
                                        showDeleteDialog = true
                                    },
                                    onEditClicked = { item ->
                                        isEditingItem = true
                                        indexOfEditingItem = i
                                        itemToEdit = QuoteProductsInformation(
                                            quantity = item.quantity,
                                            description = item.description,
                                            price = item.price,
                                            subtotal = ""
                                        )
                                        showQuoteChildDialog = true
                                    }
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .wrapContentWidth()
                            .align(Alignment.BottomStart)
                    ) {
                        ValidationText(
                            fillMaxWidth = false,
                            show = items.isEmpty(),
                            text = "Agregue al menos un producto"
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_add_fiestamas),
                        contentDescription = null,
                        modifier = Modifier
                            .height(28.dp.autoSize())
                            .align(Alignment.BottomEnd)
                            .clickable {
                                isEditingItem = false
                                showQuoteChildDialog = true
                            }
                    )
                }

                VerticalSpacer(height = 10.dp)
                HorizontalLine(color = Color.Gray, thick = 0.5.dp)
                VerticalSpacer(height = 15.dp)

                Row {
                    Column(modifier = Modifier.weight(.6f)) {
                        TextRegular(
                            modifier = Modifier.padding(start = 5.dp, end = 10.dp),
                            text = "Notas importantes",
                            size = 12.sp.autoSize(),
                            align = TextAlign.Start,
                            color = Color.Gray
                        )
                        VerticalSpacer(height = 4.dp)
                        Row(
                            modifier = Modifier
                                .height(115.dp.autoSize())
                                .fillMaxWidth()
                                .padding(end = 10.dp.autoSize())
                                .background(Color.White, allRoundedCornerShape12)
                                .border(0.5.dp, Color.Gray, allRoundedCornerShape12),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            BasicTextField(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp.autoSize()),
                                value = importantNotes,
                                onValueChange = { importantNotes = it },
                                textStyle = LocalTextStyle.current.copy(
                                    fontSize = 16.sp.autoSize()
                                ),
                                singleLine = false
                            )
                        }
                    }

                    Box(modifier = Modifier
                        .weight(.4f)
                        .fillMaxWidth()
                        .height(135.dp.autoSize())
                    ) {
                        TextRegular(
                            modifier = Modifier.align(Alignment.TopEnd),
                            text = "Total      $$total",
                            size = 12.sp.autoSize(),
                            align = TextAlign.End,
                        )

                        Box(
                            modifier = Modifier
                                .background(PinkFiestamas, shape = allRoundedCornerShape16)
                                .clip(allRoundedCornerShape16)
                                .align(Alignment.BottomEnd)
                                .clickable {
                                    if (items.isNotEmpty()) {
                                        if (isEditingData) {
                                            onEditQuoteClicked(
                                                items,
                                                originalItemsSize,
                                                importantNotes,
                                                quote?.noteBook_client.orEmpty(),
                                                quote?.noteBook_provider.orEmpty(),
                                                total.toInt()
                                            )
                                        } else {
                                            onSendNewQuoteClicked(
                                                items,
                                                importantNotes,
                                                total.toInt()
                                            )
                                        }
                                    } else {
                                        showToast(context, "Agregue al menos un producto")
                                    }
                                }
                        ) {
                            TextMedium(
                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp),
                                text = "Enviar",
                                color = Color.White,
                                fillMaxWidth = false,
                                size = 18.sp.autoSize()
                            )
                        }
                    }
                }
            }
        )}
}

@Composable
fun ChildQuoteDialog(
    isCancelable: Boolean = true,
    isEditing: Boolean,
    itemToEdit: QuoteProductsInformation?,
    onSaved: (
        quantity: String,
        description: String,
        price: String
    ) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var quantity by rememberSaveable { mutableStateOf(itemToEdit?.quantity.orEmpty()) }
    var description by rememberSaveable { mutableStateOf(itemToEdit?.description.orEmpty()) }
    var price by rememberSaveable { mutableStateOf(itemToEdit?.price.orEmpty()) }

    var showValidationQty by remember { mutableStateOf(false) }
    var textValidationQty by remember { mutableStateOf("") }
    var showValidationDesc by remember { mutableStateOf(false) }
    var showValidationPrice by remember { mutableStateOf(false) }
    var textValidationPrice by remember { mutableStateOf("") }

    BaseDialog(
        isCancelable = isCancelable,
        onDismiss = onDismiss,
        content = {
            TextMedium(
                text = if (isEditing) "Editar producto" else "Nuevo producto",
                size = 20.sp.autoSize()
            )

            VerticalSpacer(height = 10.dp)
            HorizontalLine(color = Color.Gray, thick = 0.5.dp)
            VerticalSpacer(height = 10.dp)

            RoundedEdittext(
                placeholder = "Cantidad",
                value = quantity,
                keyboardType = KeyboardType.Number
            ) {
                quantity = it
                showValidationQty = it.isBlank()
                textValidationQty = context.getString(R.string.gral_error_empty_female, "La Cantidad")
                if (it.isNotEmpty() && it.toInt() < 1) {
                    textValidationQty = context.getString(R.string.gral_error_zero, "La cantidad")
                    showValidationQty = true
                }
            }

            ValidationText(show = showValidationQty, text = textValidationQty)

            VerticalSpacer(height = 5.dp)

            RoundedEdittext(
                placeholder = "Descripción",
                value = description
            ) {
                description = it
                showValidationDesc = it.isBlank() || it.isBlank()
            }

            ValidationText(show = showValidationDesc, text = context.getString(R.string.gral_error_empty_female, "La Descripción"))

            VerticalSpacer(height = 5.dp)

            RoundedEdittext(
                placeholder = "Precio",
                value = price,
                keyboardType = KeyboardType.Number
            ) {
                price = it
                showValidationPrice = price.isBlank()
                textValidationPrice = context.getString(R.string.gral_error_empty, "El Precio")
                if (it.isNotEmpty() && it.toInt() < 1) {
                    textValidationPrice = context.getString(R.string.gral_error_zero, "El Precio")
                    showValidationPrice = true
                }
            }

            ValidationText(show = showValidationPrice, text = textValidationPrice)

            VerticalSpacer(height = 22.dp)

            Box(
                modifier = Modifier
                    .background(PinkFiestamas, shape = allRoundedCornerShape16)
                    .clip(allRoundedCornerShape16)
                    .clickable {
                        if (quantity.isBlank()) {
                            showToast(
                                context,
                                context.getString(R.string.gral_error_empty_female, "La Cantidad")
                            )
                            return@clickable
                        }
                        if (quantity.isNotEmpty() && quantity.toInt() < 1) {
                            showToast(context, "La Cantidad no puede ser 0")
                            return@clickable
                        }
                        if (description.isBlank()) {
                            showToast(
                                context,
                                context.getString(
                                    R.string.gral_error_empty_female,
                                    "La Descripción"
                                )
                            )
                            return@clickable
                        }
                        if (price.isBlank()) {
                            showToast(
                                context,
                                context.getString(R.string.gral_error_empty, "El Precio")
                            )
                            return@clickable
                        }
                        if (price.isNotEmpty() && price.toInt() < 1) {
                            showToast(context, "El Precio no puede ser 0")
                            return@clickable
                        }
                        onSaved(quantity, description, price)
                    }
            ) {
                TextMedium(
                    modifier = Modifier.padding(15.dp.autoSize()),
                    text = "Guardar",
                    color = Color.White,
                    size = 18.sp.autoSize()
                )
            }

            VerticalSpacer(height = 5.dp)
        }
    )
}

@Preview
@Composable
fun NewQuoteDialogPreview() {
    NewQuoteOrEditDialog(
        isVisible = true,
        isCancelable = false,
        isEditingData = false,
        editableList = null,
        quote = null,
        onSendNewQuoteClicked = { _, _, _ -> },
        onEditQuoteClicked = { _, _, _, _, _, _ -> },
        onDismiss = {}
    )
}

@Preview
@Composable
fun ChildQuoteDialogPreview() {
    ChildQuoteDialog(
        isEditing = false,
        itemToEdit = null,
        onSaved = { _, _, _ ->},
        onDismiss = {}
    )
}
