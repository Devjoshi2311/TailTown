package com.tailtown.pawcare.ui.account

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tailtown.pawcare.ui.theme.Bone
import com.tailtown.pawcare.ui.theme.Coral
import com.tailtown.pawcare.ui.theme.CoralSoft
import com.tailtown.pawcare.ui.theme.Hairline
import com.tailtown.pawcare.ui.theme.Ink500
import com.tailtown.pawcare.ui.theme.Ink900
import com.tailtown.pawcare.ui.theme.PawcareTheme
import com.tailtown.pawcare.ui.theme.PillShape
import com.tailtown.pawcare.ui.theme.Teal600
import com.tailtown.pawcare.ui.theme.White

@Composable
fun ReferFriendScreen(
    referral: ReferralInfo = sampleReferral,
    onShareCode: () -> Unit = {},
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize().background(Bone).navigationBarsPadding()) {
        Column(modifier = Modifier.fillMaxWidth().background(White).statusBarsPadding()) {
            IconButton(onClick = onBack, modifier = Modifier.padding(4.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Ink900)
            }
            Text(
                "Refer a friend",
                style = MaterialTheme.typography.displayLarge,
                color = Ink900,
                modifier = Modifier.padding(horizontal = 24.dp).padding(bottom = 20.dp),
            )
            HorizontalDivider(color = Hairline)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Hero reward card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(CoralSoft)
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("🎁", fontSize = 40.sp)
                Spacer(Modifier.height(12.dp))
                Text(
                    "Give ₹${referral.refereeReward}, Get ₹${referral.referrerReward}",
                    style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = Ink900,
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Your friend gets ₹${referral.refereeReward} off their first order.\nYou earn ₹${referral.referrerReward} in wallet credits.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Ink500,
                    textAlign = TextAlign.Center,
                )
            }

            // Referral code card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(White)
                    .border(1.dp, Hairline, RoundedCornerShape(16.dp))
                    .padding(20.dp),
            ) {
                Text("Your referral code", style = MaterialTheme.typography.labelSmall, color = Ink500)
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        referral.code,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 3.sp,
                        ),
                        color = Coral,
                    )
                    Box(
                        modifier = Modifier
                            .clip(PillShape)
                            .background(Bone)
                            .clickable(onClick = onShareCode)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                    ) {
                        Text("Copy", style = MaterialTheme.typography.labelSmall, color = Ink900)
                    }
                }
            }

            // Stats row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(White)
                    .border(1.dp, Hairline, RoundedCornerShape(16.dp))
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                StatCell(label = "Referrals made", value = "${referral.referralsMade}")
                Box(modifier = Modifier.padding(horizontal = 8.dp).background(Hairline).fillMaxWidth(0.002f))
                StatCell(label = "Rewards earned", value = "₹${referral.rewardsEarned}")
            }
        }

        // Share button
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(White)
                .navigationBarsPadding()
                .padding(horizontal = 24.dp, vertical = 16.dp),
        ) {
            Button(
                onClick = onShareCode,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = PillShape,
                colors = ButtonDefaults.buttonColors(containerColor = Ink900, contentColor = White),
            ) {
                Text("Share invite link", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium))
            }
        }
    }
}

@Composable
private fun StatCell(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold), color = Teal600)
        Text(label, style = MaterialTheme.typography.labelSmall, color = Ink500)
    }
}

@Preview(showBackground = true)
@Composable
private fun ReferFriendPreview() {
    PawcareTheme { ReferFriendScreen(onBack = {}) }
}
