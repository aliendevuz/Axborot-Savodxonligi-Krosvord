package uz.alien.askrosvord

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material.icons.filled.KeyboardHide
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HintItem(
    word: CrosswordWord,
    isSelected: Boolean,
    onClick: () -> Unit,
    onReveal: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(if (isSelected) 8.dp else 2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${word.number}. ${word.hint}",
                    fontSize = 15.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${word.word.length} harf",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
            }

            IconButton(
                onClick = onReveal,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Javobni ko'rsatish",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun VirtualKeyboard(
    onKeyPress: (Char) -> Unit,
    onDelete: () -> Unit,
    onToggleVisibility: () -> Unit,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val latinKeys = listOf(
        listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P'),
        listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L'),
        listOf(Char(1), 'Z', 'X', 'C', 'V', 'B', 'N', 'M', '\'', Char(0))
    )

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val horizontalPadding = 12.dp
    val rowSpacing = 6.dp
    val availableWidth = screenWidth - horizontalPadding * 2

    val firstRowButtonCount = latinKeys[0].size
    val firstRowTotalSpacing = rowSpacing * (firstRowButtonCount - 1)
    val firstRowButtonWidth = (availableWidth - firstRowTotalSpacing) / firstRowButtonCount

    val keyboardContentHeight = 180.dp
    val animatedContentHeight by animateDpAsState(
        targetValue = if (isVisible) keyboardContentHeight else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "KeyboardContentHeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = horizontalPadding, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(rowSpacing)
    ) {
        if (animatedContentHeight > 0.dp) {
            Column(
                modifier = Modifier
                    .height(animatedContentHeight)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(rowSpacing)
            ) {
                latinKeys.forEachIndexed { rowIndex, row ->
                    val buttonCount = row.size
                    val totalSpacing = rowSpacing * (buttonCount - 1)
                    val remainingWidth = availableWidth - totalSpacing
                    val buttonWidth = if (rowIndex == 0) {
                        remainingWidth / buttonCount
                    } else {
                        firstRowButtonWidth
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(rowSpacing, Alignment.CenterHorizontally)
                    ) {
                        row.forEach { char ->
                            when (char) {
                                Char(0) -> {
                                    Button(
                                        onClick = onDelete,
                                        modifier = Modifier
                                            .width(buttonWidth)
                                            .height(52.dp)
                                            .shadow(4.dp, RoundedCornerShape(12.dp)),
                                        contentPadding = PaddingValues(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        ),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = "O'chirish",
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Char(1) -> {
                                    Button(
                                        onClick = { },
                                        modifier = Modifier
                                            .width(buttonWidth)
                                            .height(52.dp)
                                            .alpha(0f),
                                        contentPadding = PaddingValues(4.dp)
                                    ) {}
                                }
                                else -> {
                                    KeyboardButton(
                                        text = char.toString(),
                                        onClick = { onKeyPress(char) },
                                        width = buttonWidth
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        val toggleText = if (isVisible) "Yashirish" else "Ko'rsatish"
        val toggleIcon = if (isVisible) Icons.Default.KeyboardHide else Icons.Default.Keyboard

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onToggleVisibility,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(52.dp)
                    .shadow(4.dp, RoundedCornerShape(12.dp)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = toggleText,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = toggleText,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun KeyboardButton(
    text: String,
    onClick: () -> Unit,
    width: Dp
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(width)
            .height(52.dp)
            .shadow(4.dp, RoundedCornerShape(12.dp)),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun ControlPanel(
    isHorizontalMode: Boolean,
    showHints: Boolean,
    correctCells: Int,
    totalCells: Int,
    onToggleDirection: () -> Unit,
    onToggleHints: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jarayon:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "$correctCells / $totalCells",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { if (totalCells > 0) correctCells.toFloat() / totalCells else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )
        }
    }
}