package de.rki.coronawarnapp.ui

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.lifecycleScope
import de.rki.coronawarnapp.storage.LocalData
import de.rki.coronawarnapp.ui.main.MainActivity
import de.rki.coronawarnapp.ui.onboarding.OnboardingActivity
import de.rki.coronawarnapp.update.UpdateChecker
import kotlinx.coroutines.launch

class LauncherActivity : AppCompatActivity() {
    companion object {
        private val TAG: String? = LauncherActivity::class.simpleName
        private const val PCR_LENGTH: Int = 16
    }

    private lateinit var updateChecker: UpdateChecker

    override fun onResume() {
        super.onResume()

        updateChecker = UpdateChecker(this)
        lifecycleScope.launch {
            updateChecker.checkForUpdate()
        }
    }

    fun navigateToActivities() {
        if (LocalData.isOnboarded()) {
            val data: Uri? = intent?.data
            if (isPcrValid(data)) {
                startMainActivityWithTestActivivation(data.toString())
            } else {
                startMainActivity()
            }
        } else {
            startOnboardingActivity()
        }
    }

    private fun isPcrValid(data: Uri?): Boolean {
        val pcr = data?.getQueryParameter("pcr")

        return (pcr != null && pcr.length == PCR_LENGTH && pcr.isDigitsOnly() && data.queryParameterNames.size == 1)
    }

    private fun startOnboardingActivity() {
        OnboardingActivity.start(this)
        this.overridePendingTransition(0, 0)
        finish()
    }

    private fun startMainActivity() {
        MainActivity.start(this)
        this.overridePendingTransition(0, 0)
        finish()
    }

    private fun startMainActivityWithTestActivivation(url: String) {
        MainActivity.startForTestActivation(this, url)
        this.overridePendingTransition(0, 0)
        finish()
    }
}
