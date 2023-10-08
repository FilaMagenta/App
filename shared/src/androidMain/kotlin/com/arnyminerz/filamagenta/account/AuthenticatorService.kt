package com.arnyminerz.filamagenta.account

import android.app.Service
import android.content.Intent
import android.os.IBinder

class AuthenticatorService: Service() {
    override fun onBind(intent: Intent?): IBinder = Authenticator(this).iBinder
}
