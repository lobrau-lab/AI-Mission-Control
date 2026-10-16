package com.example.data.security

import android.util.Log
import java.security.MessageDigest
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class BossSignatureStamp(
    val signatureHex: String,
    val timestampMs: Long,
    val nonce: String,
    val formattedStamp: String,
    val isVerifiedBoss: Boolean = true
)

object BossSecurityManager {
    private const val TAG = "BossSecurityManager"
    var bossSecretKey: String = "BOSS_MASTER_OPERATOR_KEY_v2.5_ALPHA"

    /**
     * Stamps an outgoing user command with an authentic cryptographic Boss Input Signature.
     */
    fun stampCommand(slot: Int, commandText: String): Pair<String, BossSignatureStamp> {
        val timestampMs = System.currentTimeMillis()
        val nonce = (100000..999999).random().toString()
        val rawPayload = "BOSS_CMD|$slot|$timestampMs|$nonce|$commandText"
        val sigHex = generateHmacSha256(rawPayload, bossSecretKey).take(16)

        val formattedStamp = "\u001B[38;2;168;85;247m[BOSS_SIG: $sigHex | NONCE: $nonce | AUTH: VERIFIED_BOSS]\u001B[0m"
        val stampedCommand = "$commandText\n/* $formattedStamp */"

        val stamp = BossSignatureStamp(
            signatureHex = sigHex,
            timestampMs = timestampMs,
            nonce = nonce,
            formattedStamp = formattedStamp,
            isVerifiedBoss = true
        )

        return Pair(stampedCommand, stamp)
    }

    fun verifySignature(slot: Int, timestampMs: Long, nonce: String, commandText: String, candidateSig: String): Boolean {
        val rawPayload = "BOSS_CMD|$slot|$timestampMs|$nonce|$commandText"
        val expectedSig = generateHmacSha256(rawPayload, bossSecretKey).take(16)
        return candidateSig.equals(expectedSig, ignoreCase = true)
    }

    private fun generateHmacSha256(data: String, key: String): String {
        return try {
            val secretKey = SecretKeySpec(key.toByteArray(Charsets.UTF_8), "HmacSHA256")
            val mac = Mac.getInstance("HmacSHA256")
            mac.init(secretKey)
            val hmacData = mac.doFinal(data.toByteArray(Charsets.UTF_8))
            hmacData.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            Log.e(TAG, "HMAC generation failed, fallback to SHA-256: ${e.message}")
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest((data + key).toByteArray(Charsets.UTF_8))
            digest.joinToString("") { "%02x".format(it) }
        }
    }
}

data class SelfHealingReport(
    val healedSessionsCount: Int,
    val healedNodesCount: Int,
    val diagnosticLogs: List<String>,
    val timestampMs: Long = System.currentTimeMillis()
)

object SelfHealingManager {
    fun createDiagnosticRecoveryLog(ttySlot: Int, reason: String, recoveryNode: String): String {
        val timeStr = SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
        return "\u001B[38;2;239;68;68m[SELF-HEALING AUTOMATION @ $timeStr]\u001B[0m TTY 0$ttySlot anomaly detected: '$reason'. Re-linking agent daemon to $recoveryNode... \u001B[32m[RECOVERY COMPLETE]\u001B[0m"
    }
}
