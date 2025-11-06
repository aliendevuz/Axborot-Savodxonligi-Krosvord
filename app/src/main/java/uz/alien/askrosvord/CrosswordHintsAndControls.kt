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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun HintsPanel(
    words: List<CrosswordWord>,
    selectedWord: CrosswordWord?,
    showHints: Boolean,
    onWordClick: (CrosswordWord) -> Unit,
    onRevealWord: (CrosswordWord) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!showHints) return

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Gorizontal", "Vertikal")

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        val filteredWords = words.filter {
            it.isHorizontal == (selectedTab == 0)
        }.sortedBy { it.number }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 300.dp)
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredWords) { word ->
                HintItem(
                    word = word,
                    isSelected = selectedWord == word,
                    onClick = { onWordClick(word) },
                    onReveal = { onRevealWord(word) }
                )
            }
        }
    }
}

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
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 4.dp else 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${word.number}. ${word.hint}",
                    fontSize = 14.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = "${word.word.length} harf",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            IconButton(
                onClick = onReveal,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lightbulb,
                    contentDescription = "Javobni ko'rsatish",
                    tint = MaterialTheme.colorScheme.primary
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
    val horizontalPadding = 8.dp
    val rowSpacing = 4.dp
    val availableWidth = screenWidth - horizontalPadding * 2

    val firstRowButtonCount = latinKeys[0].size
    val firstRowTotalSpacing = rowSpacing * (firstRowButtonCount - 1)
    val firstRowButtonWidth = (availableWidth - firstRowTotalSpacing) / firstRowButtonCount

    val keyboardContentHeight = 172.dp
    val animatedContentHeight by animateDpAsState(
        targetValue = if (isVisible) keyboardContentHeight else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "KeyboardContentHeight"
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = horizontalPadding, vertical = 8.dp),
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
                                            .height(48.dp),
                                        contentPadding = PaddingValues(4.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = MaterialTheme.colorScheme.error
                                        )
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Backspace,
                                            contentDescription = "O'chirish",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Char(1) -> {
                                    Button(
                                        onClick = { },
                                        modifier = Modifier
                                            .width(buttonWidth)
                                            .height(48.dp)
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

        val toggleText = if (isVisible) "Yashirish" else "Ko‘rsatish"
        val toggleIcon = if (isVisible) Icons.Default.KeyboardHide else Icons.Default.Keyboard

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onToggleVisibility,
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                )
            ) {
                Icon(
                    imageVector = toggleIcon,
                    contentDescription = toggleText
                )
                Spacer(Modifier.width(4.dp))
                Text(toggleText)
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
            .height(48.dp),
        contentPadding = PaddingValues(4.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
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
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Jarayon:",
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$correctCells / $totalCells",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            LinearProgressIndicator(
                progress = { if (totalCells > 0) correctCells.toFloat() / totalCells else 0f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = ProgressIndicatorDefaults.linearTrackColor,
                strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
            )

            HorizontalDivider(Modifier, DividerDefaults.Thickness, DividerDefaults.color)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onToggleDirection,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = if (isHorizontalMode)
                            Icons.AutoMirrored.Filled.ArrowForward
                        else
                            Icons.Default.ArrowDownward,
                        contentDescription = null
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(if (isHorizontalMode) "Gorizontal" else "Vertikal")
                }

                IconButton(
                    onClick = onToggleHints,
                    modifier = Modifier
                        .background(
                            if (showHints) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.surface,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Help,
                        contentDescription = "Maslahatlar",
                        tint = if (showHints) Color.White else Color.Gray
                    )
                }

                IconButton(
                    onClick = onReset,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.errorContainer,
                            shape = MaterialTheme.shapes.small
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Qaytadan boshlash",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }
    }
}