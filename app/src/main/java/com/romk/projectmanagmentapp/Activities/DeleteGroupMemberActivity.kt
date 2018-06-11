package com.romk.projectmanagmentapp.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.romk.projectmanagmentapp.Adapters.DeleteGroupMembersAdapter
import com.romk.projectmanagmentapp.Models.SessionModel
import com.romk.projectmanagmentapp.NetworkConnection.HttpDeleteRequestHandler
import com.romk.projectmanagmentapp.R
import com.romk.projectmanagmentapp.Utils
import org.json.JSONObject

class DeleteGroupMemberActivity: AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var groupId = 0
    private lateinit var memberIds: ArrayList<Int>
    private lateinit var memberEmails: ArrayList<String>
    private var leaderId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_group_member)
        setSupportActionBar(findViewById(R.id.delete_member_toolbar))
        groupId = intent.extras.getInt("groupId")
        memberIds = intent.extras.getIntegerArrayList("memberIds")
        memberEmails = intent.extras.getStringArrayList("memberEmails")
        leaderId = intent.extras.getInt("leaderId")

        setRecyclerView()
        bindButtons()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.basic_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId){
        R.id.menu_logout -> {
            Utils().deleteSession(this)
            true
        }
        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun bindButtons() {
        val deleteMemberButton = findViewById<Button>(R.id.button_delete_member)
        deleteMemberButton.setOnClickListener {
            if((viewAdapter as DeleteGroupMembersAdapter).getPositionsOfselectedItems().isNotEmpty()) {
                deleteMembers()
            }
            else {
                Toast.makeText(this, "You need to check at least one member", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setRecyclerView() {
        viewManager = LinearLayoutManager(this)

        var leaderPos = -1

        for (i in 0 until memberIds.size) {
            if (memberIds[i] == leaderId) {
                leaderPos = i
            }
        }

        viewAdapter = DeleteGroupMembersAdapter(leaderPos, memberEmails)

        recyclerView = findViewById<RecyclerView>(R.id.delete_member_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getJsonString(id: Int): String {
        val json = JSONObject().put("user_id", memberIds[id])
        return json.toString()
    }

    private fun deleteMembers() {
        val ids = (viewAdapter as DeleteGroupMembersAdapter).getPositionsOfselectedItems()
        var is_all_passed = true
        var bad_code = 0
        for (id in ids) {
            val connection = HttpDeleteRequestHandler().execute(
                "http://kanban-project-management-api.herokuapp.com/v1/groups/$groupId/remove_user_from_group",
                SessionModel.instance.email,
                SessionModel.instance.token,
                getJsonString(id)
            )
            if(connection.get().first != 200) {
                is_all_passed = false
                bad_code = connection.get().first
            }
        }
        if (is_all_passed) {
            Toast.makeText(this, "All members has been succesfuly deleted from group", Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this, "At least one member hasn't been removed, code $bad_code", Toast.LENGTH_SHORT).show()
        }
        val groupMemberActivityIntent = Intent(this, GroupMembersActivity::class.java)
        groupMemberActivityIntent.putExtra("groupId", groupId)
        startActivity(groupMemberActivityIntent)
    }
}