package com.example.android.nerdlauncher

import android.content.Intent
import android.content.pm.ResolveInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
private const val TAG = "NerdLauncherActivity"
class NerdLauncherActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nerd_launcher)

        recyclerView = findViewById(R.id.app_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupAdapter()
    }

    private fun setupAdapter(){
        val startupIntent= Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val activities = packageManager.queryIntentActivities(startupIntent,0)
        activities.sortWith(Comparator{ a,b ->
            String.CASE_INSENSITIVE_ORDER.compare(
                a.loadLabel(packageManager).toString(),
                b.loadLabel(packageManager).toString()
            )
        })
        Log.i(TAG,"found ${activities.size} activities")
        recyclerView.adapter = ActivityAdapter(activities)
    }

    private class ActivityHolder(view: View): RecyclerView.ViewHolder(view),View.OnClickListener{
        private val nameTextView: TextView = itemView.findViewById(R.id.app_name)
        private val iconImageView: ImageView = itemView.findViewById(R.id.app_icon)
        private lateinit var resolveInfo: ResolveInfo
        init {
            itemView.setOnClickListener(this)
        }
        fun bindActivity(resolveInfo: ResolveInfo){
            this.resolveInfo=resolveInfo
            val packageManager = itemView.context.packageManager
            val appName = resolveInfo.loadLabel(packageManager).toString()
            val icon = resolveInfo.loadIcon(packageManager)
            nameTextView.text = appName
            iconImageView.setImageDrawable(icon)
        }

        override fun onClick(view: View?) {
            val activityInfo = resolveInfo.activityInfo
            val intent = Intent(Intent.ACTION_MAIN).apply {
                setClassName(activityInfo.applicationInfo.packageName,activityInfo.name)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val context = view!!.context
            context.startActivity(intent)
        }
    }

    private class ActivityAdapter(val activities: List<ResolveInfo>): RecyclerView.Adapter<ActivityHolder>(){
        override fun onCreateViewHolder(container: ViewGroup, viewType: Int): ActivityHolder {
            val layoutInflater = LayoutInflater.from(container.context)
            val view = layoutInflater.inflate(R.layout.list_app,container,false)
            return ActivityHolder(view)
        }

        override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
            val resolveInfo = activities[position]
            holder.bindActivity(resolveInfo)
        }

        override fun getItemCount(): Int {
            return activities.size
        }
    }
}
