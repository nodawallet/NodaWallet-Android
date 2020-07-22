package com.application.nodawallet.utils

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions


class PermissionUtils{

    private val REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124


    fun permissionList(permissionList: Array<out String>,activity: Activity,action:PermissionCheck){

        if (!hasPermissions(activity, *permissionList)){
            requestPermissions(activity,permissionList,REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS)
        }
        else {
            action.onSuccess("Success")
        }
    }

    fun hasPermissions(context: Context, vararg permissions: String): Boolean = permissions.all {
        ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
    }


    interface PermissionCheck{

        fun onSuccess(result:String?)
    }



}