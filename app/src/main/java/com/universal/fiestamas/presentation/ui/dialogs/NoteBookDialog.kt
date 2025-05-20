package com.universal.fiestamas.presentation.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.universal.fiestamas.presentation.theme.PinkFiestamas
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape12
import com.universal.fiestamas.presentation.theme.allRoundedCornerShape16
import com.universal.fiestamas.presentation.ui.HorizontalLine
import com.universal.fiestamas.presentation.ui.TextMedium
import com.universal.fiestamas.presentation.ui.VerticalSpacer
import com.universal.fiestamas.presentation.ui.calendar.clickable

@Composable
fun NoteBookDialog(
    isVisible: Boolean,
    type: NotesType,
    savedNotes: String,
    onSaveClicked: (String) -> Unit,
    onDismiss: () -> Unit
) {
    if (isVisible) {
        var notes by rememberSaveable { mutableStateOf(savedNotes) }
        val title = when (type) {
            NotesType.PERSONAL_CLIENT -> "Block de notas"
            NotesType.PERSONAL_PROVIDER -> "Block de notas"
            NotesType.IMPORTANT -> "Notas importantes"
        }

        BaseDialog(
            isCancelable = true,
            onDismiss = onDismiss,
            content = {
                TextMedium(text = title, size = 20.sp)

                VerticalSpacer(height = 10.dp)
                HorizontalLine(color = Color.Gray, thick = 0.5.dp)
                VerticalSpacer(height = 15.dp)

                Row(
                    modifier = Modifier
                        .height(115.dp)
                        .fillMaxWidth()
                        .padding(end = 10.dp)
                        .background(Color.White, allRoundedCornerShape12)
                        .border(0.5.dp, Color.Gray, allRoundedCornerShape12),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    BasicTextField(
                        modifier = Modifier.fillMaxSize().padding(10.dp),
                        value = notes,
                        onValueChange = { notes = it },
                        singleLine = false,
                    )
                }

                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 12.dp).padding(horizontal = 5.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(PinkFiestamas, shape = allRoundedCornerShape16)
                            .clip(allRoundedCornerShape16)
                            .align(Alignment.CenterEnd)
                            .clickable { onSaveClicked(notes) }
                    ) {
                        TextMedium(
                            modifier = Modifier.padding(vertical = 8.dp, horizontal = 20.dp),
                            text = "Guardar",
                            color = Color.White,
                            fillMaxWidth = false,
                            size = 18.sp
                        )
                    }
                }
            }
        )
    }
}

@Preview
@Composable
fun NoteBookDialogPreview() {
    NoteBookDialog(
        isVisible = true,
        savedNotes = "",
        type = NotesType.PERSONAL_CLIENT,
        onSaveClicked = { },
        onDismiss = { }
    )
}


enum class NotesType {
    PERSONAL_CLIENT,
    PERSONAL_PROVIDER,
    IMPORTANT
}