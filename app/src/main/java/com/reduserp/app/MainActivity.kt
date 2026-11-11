package com.reduserp.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.security.MessageDigest

private val Bg = Color(0xFF090909)
private val Panel = Color(0xFF171717)
private val Red = Color(0xFFE53935)
private val Muted = Color(0xFFAAAAAA)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RedUserApp() }
    }
}

private data class Tool(val titleEn: String, val titleHi: String, val descEn: String, val descHi: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
fun RedUserApp() {
    var hindi by remember { mutableStateOf(false) }
    var selected by remember { mutableStateOf<String?>(null) }
    val tools = listOf(
        Tool("Password Audit", "पासवर्ड जांच", "Check password strength locally", "पासवर्ड की मजबूती फोन पर जांचें", Icons.Default.Key),
        Tool("SHA-256 Hash", "SHA-256 हैश", "Generate a SHA-256 digest", "SHA-256 हैश बनाएं", Icons.Default.Tag),
        Tool("DNS Lookup", "DNS लुकअप", "Resolve a domain you are authorized to inspect", "अनुमति वाले डोमेन का DNS देखें", Icons.Default.Language),
        Tool("Security Checklist", "सुरक्षा चेकलिस्ट", "Practical account and device safety steps", "अकाउंट और डिवाइस सुरक्षा के कदम", Icons.Default.VerifiedUser),
        Tool("Phone Recovery", "फोन रिकवरी", "Official owner recovery options", "मालिक के लिए आधिकारिक रिकवरी विकल्प", Icons.Default.PhoneAndroid),
        Tool("About & Legal", "ऐप जानकारी", "Authorized use and limitations", "अनुमति-आधारित उपयोग और सीमाएं", Icons.Default.Info)
    )
    MaterialTheme(colorScheme = darkColorScheme(
        primary = Red, background = Bg, surface = Panel,
        onPrimary = Color.White, onBackground = Color.White, onSurface = Color.White
    )) {
        Surface(Modifier.fillMaxSize(), color = Bg) {
            Column(Modifier.fillMaxSize().padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(Modifier.size(48.dp).background(Red, RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Shield, null, tint = Color.White, modifier = Modifier.size(30.dp))
                    }
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("RedUser-P", fontSize = 25.sp, fontWeight = FontWeight.Black)
                        Text("CYBERSECURITY TOOLKIT  •  By Prem", color = Muted, fontSize = 10.sp, letterSpacing = 1.sp)
                    }
                    TextButton(onClick = { hindi = !hindi }) { Text(if (hindi) "EN" else "हि", color = Red) }
                }
                Spacer(Modifier.height(20.dp))
                if (selected == null) {
                    Text(if (hindi) "आपका सुरक्षा डैशबोर्ड" else "Your Security Dashboard", fontSize = 21.sp, fontWeight = FontWeight.Bold)
                    Text(if (hindi) "केवल अपने या अनुमति प्राप्त सिस्टम पर उपयोग करें।" else "Use only on systems you own or are authorized to test.", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(16.dp))
                    LazyVerticalGrid(columns = GridCells.Fixed(2), verticalArrangement = Arrangement.spacedBy(12.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(tools) { tool ->
                            Column(
                                Modifier.fillMaxWidth().height(142.dp).background(Panel, RoundedCornerShape(16.dp))
                                    .clickable { selected = tool.titleEn }.padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Icon(tool.icon, null, tint = Red, modifier = Modifier.size(28.dp))
                                Column {
                                    Text(if (hindi) tool.titleHi else tool.titleEn, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(if (hindi) tool.descHi else tool.descEn, color = Muted, fontSize = 10.sp, lineHeight = 14.sp)
                                }
                            }
                        }
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { selected = null }) { Icon(Icons.Default.ArrowBack, "Back", tint = Red) }
                        Text(if (hindi) tools.firstOrNull { it.titleEn == selected }?.titleHi ?: selected!! else selected!!, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(10.dp))
                    ToolScreen(selected!!, hindi)
                }
            }
        }
    }
}

@Composable
private fun ToolScreen(tool: String, hindi: Boolean) {
    val scope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    var result by remember { mutableStateOf("") }
    Column(Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        when (tool) {
            "Password Audit" -> {
                Text(if (hindi) "पासवर्ड यहां डिवाइस पर ही जांचा जाता है; कहीं भेजा नहीं जाता।" else "Your password is evaluated locally and is not uploaded.", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(input, { input = it; result = "" }, label = { Text(if (hindi) "पासवर्ड दर्ज करें" else "Enter password") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Spacer(Modifier.height(12.dp))
                Button(onClick = {
                    val score = listOf(input.length >= 12, input.any { it.isLowerCase() }, input.any { it.isUpperCase() }, input.any { it.isDigit() }, input.any { !it.isLetterOrDigit() }).count { it }
                    result = when {
                        input.isEmpty() -> if (hindi) "पहले पासवर्ड दर्ज करें।" else "Enter a password first."
                        score <= 2 -> if (hindi) "कमजोर — लंबा और यूनिक पासवर्ड चुनें।" else "Weak — choose a longer, unique passphrase."
                        score <= 4 -> if (hindi) "ठीक — इसे और मजबूत बनाएं।" else "Fair — improve length and uniqueness."
                        else -> if (hindi) "अच्छा — हर अकाउंट के लिए अलग पासवर्ड रखें।" else "Good — use a unique password for every account."
                    }
                }) { Text(if (hindi) "जांचें" else "Check strength") }
            }
            "SHA-256 Hash" -> {
                OutlinedTextField(input, { input = it }, label = { Text(if (hindi) "टेक्स्ट दर्ज करें" else "Enter text") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
                    result = bytes.joinToString("") { "%02x".format(it) }
                }, modifier = Modifier.padding(top = 12.dp)) { Text(if (hindi) "हैश बनाएं" else "Generate hash") }
            }
            "DNS Lookup" -> {
                Text(if (hindi) "केवल उस डोमेन का उपयोग करें जिसे जांचने की अनुमति है।" else "Only query domains you are authorized to inspect.", color = Muted, fontSize = 13.sp)
                OutlinedTextField(input, { input = it }, label = { Text("Domain (example.com)") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = {
                    result = if (input.isBlank()) "Enter a domain." else "Looking up..."
                    scope.launch {
                        result = try {
                            withContext(Dispatchers.IO) {
                                InetAddress.getAllByName(input.trim()).joinToString("\n") { it.hostAddress ?: "" }
                            }
                        } catch (e: Exception) { "Lookup failed: ${e.localizedMessage ?: "check domain and connection"}" }
                    }
                }, modifier = Modifier.padding(top = 12.dp)) { Text(if (hindi) "DNS जांचें" else "Resolve DNS") }
            }
            "Security Checklist" -> {
                val items = listOf(
                    "Enable two-factor authentication",
                    "Use a unique password for every account",
                    "Keep Android and apps updated",
                    "Review app permissions",
                    "Back up important files",
                    "Use screen lock and device encryption"
                )
                var checked by remember { mutableStateOf(setOf<Int>()) }
                items.forEachIndexed { i, label ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(checked = i in checked, onCheckedChange = { checked = if (it) checked + i else checked - i }, colors = CheckboxDefaults.colors(checkedColor = Red))
                        Text(label, fontSize = 13.sp)
                    }
                }
                Text("${checked.size}/${items.size} completed", color = Red, fontWeight = FontWeight.Bold)
            }
            "Phone Recovery" -> {
                Text(if (hindi) "अगर फोन आपका है, तो निर्माता की आधिकारिक रिकवरी प्रक्रिया, Google खाते, बैकअप और अधिकृत सर्विस सेंटर का उपयोग करें। डेटा मिटाने से पहले बैकअप विकल्प जांचें।" else "For a phone you own, use the manufacturer's official recovery process, the linked Google account, available backups, and an authorized service center. Check backup options before any factory reset.", color = Muted, lineHeight = 20.sp)
            }
            else -> {
                Text(if (hindi) "RedUser-P एक authorized-use cybersecurity toolkit है। यह account break-in, credential theft या security bypass नहीं करता।" else "RedUser-P is an authorized-use cybersecurity toolkit. It does not break into accounts, steal credentials, or bypass device security.", color = Muted, lineHeight = 20.sp)
            }
        }
        if (result.isNotBlank()) {
            Spacer(Modifier.height(16.dp))
            Text(if (hindi) "परिणाम" else "Result", color = Red, fontWeight = FontWeight.Bold)
            Text(result, color = Color.White, modifier = Modifier.padding(top = 6.dp))
        }
    }
}
