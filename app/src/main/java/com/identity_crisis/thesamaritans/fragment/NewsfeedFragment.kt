package com.identity_crisis.thesamaritans.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout.OnRefreshListener
import androidx.viewpager2.widget.ViewPager2
import com.google.firebase.database.*
import com.identity_crisis.thesamaritans.R
import com.identity_crisis.thesamaritans.adapter.NewsfeedAdapter
import com.identity_crisis.thesamaritans.adapter.NewsfeedRefreshAdapter
import com.identity_crisis.thesamaritans.model.Data
import com.identity_crisis.thesamaritans.model.UserList
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_newsfeed.*


class NewsfeedFragment : Fragment() {

    private lateinit var recyclerView: ViewPager2
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    private lateinit var swipeToRefresh: SwipeRefreshLayout

    var TAG = ""
    var topId = ""
    var bottomId = ""

    var topItem: DataSnapshot? = null
    var bottomItem: DataSnapshot? = null

    val DATALIMIT = 3
    var dataCount = 0

    lateinit var layoutManager: RecyclerView.LayoutManager
    val database = FirebaseDatabase.getInstance()

    val ref = database.getReference("/newsfeed")
    var refUser = database.getReference("/users")
    val adapter = GroupAdapter<GroupieViewHolder>()
    var user = UserList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_newsfeed, container, false)
//        progressBar = view.findViewById<ProgressBar>(R.id.progressBar)
        recyclerView = view.findViewById<ViewPager2>(R.id.rcview)
        swipeToRefresh = view.findViewById(R.id.refreshNF)

//        progressBar.visibility = View.VISIBLE
        Log.d("NewsfeedFragment", "Fetching Data")
        fetchDataFirst()
        Log.d("NewsfeedFragment", "Data fetched")
//        progressBar.visibility = View.GONE

        recyclerView.adapter = adapter

        swipeToRefresh.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh() {
                refreshNew()
                swipeToRefresh.setRefreshing(false)
            }
        })

        recyclerView.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                //Log.d("PAGE", "CURR: $position")
                if (position == dataCount) {
                    var callingId = bottomId.toInt() - DATALIMIT
                    if (callingId < 0) {
                        callingId = 0
                    }
                    Log.d(
                        "REFRESH DOWN",
                        "TRYING FROM firstId = ${bottomId} and DATALIMIT = $DATALIMIT"
                    )
                    refreshOld(callingId)
                }
            }
        })

        recyclerView.adapter = adapter
        return view
    }

    private fun refreshNew() {
        TAG = "REFRESH NEW"
        Log.d(TAG, "REFRESHING NEW FROM TOP ID: ${topId}")
        ref.limitToLast(DATALIMIT).orderByKey().startAt(topId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.children.last().key.toString() != topId) {
                        adapter.clear()
                        dataCount = snapshot.children.count()
                        Log.d(
                            TAG,
                            "COUNT OF NEW DATA: ${dataCount}"
                        )
                        var counter = 1
                        snapshot.children.reversed().forEach {
                            if (counter < dataCount) {

                                if (counter == 1) {
                                    topId = it.key.toString()
                                }
                                Log.d(TAG, " KEY FETCHED: ${it.key}")
                                val fetchedData = it.getValue(Data::class.java) ?: return
                                adapter.add(NewsfeedAdapter(fetchedData, user))
                                counter++
                            } else {
                                bottomId = it.key.toString()
                                val fetchedData = it.getValue(Data::class.java) ?: return
                                adapter.add(NewsfeedAdapter(fetchedData, user))
                            }
                        }
                        adapter.add(NewsfeedRefreshAdapter())
                        Log.d(TAG, " TOP ID: $topId")
                        Log.d(TAG, "BOTTOM ID: $bottomId")
                    } else {
                        swipeToRefresh.isRefreshing = false
                        Log.d(TAG, "NO NEW DATA AVAILABLE")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }

            })
    }


    private fun refreshOld(id: Int) {
        TAG = "REFRESH OLD"
        ref.orderByKey().startAt(id.toString())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    Log.d(TAG, "ID BOTTOM: ${snapshot.children.first().key.toString()}")
                    if (snapshot.children.first().key.toString() != bottomId) {
                        val iterator = snapshot.children.iterator()
                        val tempArr = arrayListOf<DataSnapshot>()
                        while (iterator.hasNext()) {
                            val curr = iterator.next()
                            if (curr.key.toString() != bottomId) {
                                tempArr.add(curr)
                                topId = curr.key.toString()
                            } else {
                                break
                            }
                        }

                        Log.d(TAG, "DATA ARR SIZE: ${tempArr.size}")
                        if (tempArr.size > 0) {
                            Log.d(TAG, "CLEARING ADAPTER AND ADDING DATA")
                            adapter.clear()
                            dataCount = tempArr.size
                            var i = dataCount - 1
                            while (i >= 0) {
                                adapter.add(
                                    NewsfeedAdapter(
                                        tempArr[i].getValue(Data::class.java)!!,
                                        user
                                    )
                                )
                                i -= 1
                            }
                            adapter.add(NewsfeedRefreshAdapter())
                            recyclerView.setCurrentItem(0, false)
                            bottomId = snapshot.children.first().key.toString()
                            Log.d(
                                TAG,
                                "SUCCESSFUL TOP ID: ${topId} BOTTOM ID: ${bottomId}"
                            )
                        } else {
                            Log.d(TAG, "NO OLDER DATA AVAILABLE: ARR SIZE 0")
                        }


                    } else {
                        Log.d(TAG, "NO OLDER DATA AVAILABLE")
                        recyclerView.setCurrentItem(adapter.itemCount - 2, false)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }


    private fun fetchDataFirst() {
        ref.limitToLast(DATALIMIT)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("FETCH", "Fetching for first time from: ${snapshot.key}")
                    if (snapshot.hasChildren()) {
                        if (progressBarNF != null) {
                            Log.d("BAR", "Making Progress Bar Invisible")
                            progressBarNF.visibility = View.INVISIBLE
                        }
                        if (refreshNF != null) {
                            Log.d("BAR", "Making Newsfeed Visible")
                            refreshNF.visibility = View.VISIBLE
                        }

                        snapshot.children.reversed().forEach {
                            val fetchedData = it.getValue(Data::class.java) ?: return
                            refUser = database.getReference("/users").child(fetchedData.uid)
                            adapter.add(NewsfeedAdapter(fetchedData, user))
                            dataCount++
                        }
                        //fetching the last id to work with swipe up to refresh properly
                        topId = snapshot.children.last().key.toString()
                        bottomId = snapshot.children.first().key.toString()
                        adapter.add(NewsfeedRefreshAdapter())
                        Log.d("First ID", "$topId")
                        Log.d("Last ID", "${bottomId}")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                }
            })
    }

//    private fun getUser(data: Data){
//        Log.d("check", refUser.child(data.uid).equalTo("username").toString())
//        refUser.child(data.uid).addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                user = snapshot.getValue(UserList::class.java)!!
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//            }
//        })
//    }
}