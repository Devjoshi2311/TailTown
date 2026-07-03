package com.tailtown.pawcare.ui.account

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.White

@Composable
fun HelpSupportScreen(
    faqs: List<FaqItem> = sampleFaqs,
    onChatSupport: () -> Unit = {},
    onCallSupport: () -> Unit = {},
    onEmailSupport: () -> Unit = {},
    onBack: () -> Unit,
) {
    var expandedIndex by remember { mutableIntStateOf(-1) }

    Column(modifier = Modifier.fillMaxSize().background(Bone).navigationBarsPadding()) {
        Column(modifier = Modifier.fillMaxWidth().background(White).statusBarsPadding()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Text(
                "Help & support",
                style = MaterialTheme.typography.displayLarge,
                color = Ink900,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 20.dp),
            )
            HorizontalDivider(color = Hairline)
        }

        LazyColumn(contentPadding = PaddingValues(24.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Contact options
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    ContactChip(label = "Chat with us", onClick = onChatSupport, modifier = Modifier.weight(1f))
                    ContactChip(label = "Call support", onClick = onCallSupport, modifier = Modifier.weight(1f))
                    ContactChip(label = "Email us", onClick = onEmailSupport, modifier = Modifier.weight(1f))
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    "Frequently asked questions",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = Ink900,
                )
                Spacer(Modifier.height(12.dp))
            }

            // FAQ accordion
            itemsIndexed(faqs) { index, faq ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(White)
                        .border(1.dp, Hairline, RoundedCornerShape(16.dp)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedIndex = if (expandedIndex == index) -1 else index }
                            .padding(horizontal = 20.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            faq.question,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = Ink900,
                            modifier = Modifier.weight(1f).padding(end = 8.dp),
                        )
                        Icon(
                            imageVector = if (expandedIndex == index) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Ink500,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                    AnimatedVisibility(visible = expandedIndex == index) {
                        Column {
                            HorizontalDivider(color = Hairline)
                            Text(
                                faq.answer,
                                style = MaterialTheme.typography.bodyMedium,
                                color = Ink500,
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ContactChip(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CoralSoft)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium), color = Coral)
    }
}

@Preview(showBackground = true)
@Composable
private fun HelpSupportPreview() {
    PawcareTheme { HelpSupportScreen(onBack = {}) }
}
