package com.example.mangareader.fragment

import android.app.TimePickerDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.example.mangareader.adapter.FavMangaRvAdapter
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity
import com.example.mangareader.database.AppDatabase
import com.example.mangareader.service.CheckNewChaptersJobService
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class LibraryFragment : Fragment() {

    companion object {
        fun newInstance() = LibraryFragment()
    }

    private val viewModel: LibraryViewModel by viewModels()
    lateinit var favoriteMangaAdapter : FavMangaRvAdapter
    lateinit var rv : RecyclerView
    lateinit var INSTANCE : AppDatabase
    lateinit var toolbar : MaterialToolbar
    lateinit var editText : TextInputEditText
    lateinit var filter : ImageButton
    lateinit var alphabet : TextView
    lateinit var time : TextView
    lateinit var bottomSheetDialog : BottomSheetDialog
    lateinit var autoUpdateSwitch : SwitchCompat
    lateinit var customTimeButton : Button
    lateinit var fab : FloatingActionButton
    private var isAlphabeticalAscending = false
    private var isTimeAscending = false
    private var switchStateLoaded = false
    private var isJobScheduled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        INSTANCE = AppDatabase.getInstance(requireContext())

        // TODO: Use the ViewModel
        viewModel.getMangas(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_library, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        INSTANCE = AppDatabase.getInstance(requireContext())

        toolbar = view.findViewById(R.id.libraryToolbar)
        editText = toolbar.findViewById(R.id.searchEditText)
        filter = toolbar.findViewById(R.id.filterButton)

        fab = view.findViewById(R.id.updateFab)
        fab.setOnClickListener {
            viewModel.viewModelScope.launch {
                withContext(Dispatchers.IO){
                    val mangas = viewModel.mangaList.value ?: listOf()
                    viewModel.checkForUpdate(mangas, requireContext())
                }
            }
        }

        bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.fav_bottom_sheet_layout)
        alphabet = bottomSheetDialog.findViewById(R.id.alphabetical_order)!!
        time = bottomSheetDialog.findViewById(R.id.time_added)!!
        customTimeButton = bottomSheetDialog.findViewById(R.id.customTimeButton)!!
        autoUpdateSwitch = bottomSheetDialog.findViewById(R.id.autoUpdateSwitch)!!

        if (!switchStateLoaded) {
            lifecycleScope.launch {
                autoUpdateSwitch.isChecked = loadSwitchState()
                isJobScheduled = loadSwitchState()
                switchStateLoaded = true
            }
        }

        // Set the listener for the autoUpdateSwitch
        autoUpdateSwitch.setOnCheckedChangeListener { _, isChecked ->
            // Save the switch state
            saveSwitchState(isChecked)

            if (isChecked && !isJobScheduled) {
                // Schedule the CheckNewChaptersJobService if the switch is turned on and the job is not already scheduled
                scheduleJobService()
                isJobScheduled = true
            } else if (!isChecked && isJobScheduled) {
                // Cancel the CheckNewChaptersJobService if the switch is turned off and the job is currently scheduled
                cancelJobService()
                isJobScheduled = false
            }
        }

        customTimeButton.setOnClickListener {
            // Create a new instance of TimePickerDialog and return it
            val c = Calendar.getInstance()
            val hour = c.get(Calendar.HOUR_OF_DAY)
            val minute = c.get(Calendar.MINUTE)

            val timePickerDialog = TimePickerDialog(context, TimePickerDialog.OnTimeSetListener { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                // Set the job to start at the selected time
                val calendar: Calendar = Calendar.getInstance().apply {
                    timeInMillis = System.currentTimeMillis()
                    set(Calendar.HOUR_OF_DAY, selectedHour)
                    set(Calendar.MINUTE, selectedMinute)
                }

                // Schedule the CheckNewChaptersJobService
                val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
                val jobInfo = JobInfo.Builder(1, ComponentName(requireContext(), CheckNewChaptersJobService::class.java))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // Run this job when any network is available
                    .setPersisted(true) // Persist this job across device reboots
                    .setMinimumLatency(calendar.timeInMillis - System.currentTimeMillis()) // Set the minimum delay
                    .build()

                jobScheduler.schedule(jobInfo)

                Toast.makeText(context, "Check new chapters scheduled at $selectedHour:$selectedMinute", Toast.LENGTH_SHORT).show()
                Log.d("LibraryFragment", "Check new chapters scheduled at $selectedHour:$selectedMinute")
            }, hour, minute, true)

            timePickerDialog.show()
        }

// ...
        time.isSelected = true
        filter.setOnClickListener {
            bottomSheetDialog.show()
        }

        alphabet.setOnClickListener {
            if(alphabet.isSelected) isAlphabeticalAscending = !isAlphabeticalAscending
            alphabet.isSelected = true
            time.isSelected = false
            isTimeAscending = false
            time.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            if (!isAlphabeticalAscending) {
                // If currently in ascending order, sort in descending order
                val filtered = viewModel.mangaList.value?.sortedBy { it.mangaTitle }
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                alphabet.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            } else {
                // If currently in descending order, sort in ascending order
                val filtered = viewModel.mangaList.value?.sortedByDescending { it.mangaTitle }
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                alphabet.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_upward, 0)
            }
            bottomSheetDialog.dismiss()
        }
        time.setOnClickListener() {
            if(time.isSelected) isTimeAscending = !isTimeAscending
            alphabet.isSelected = false
            time.isSelected = true
            isAlphabeticalAscending = false
            alphabet.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            if (!isTimeAscending) {
                // If currently in ascending order, sort in descending order
                val filtered = viewModel.mangaList.value?.sortedByDescending { it.lastAdded}
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                time.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_downward, 0)
            } else {
                // If currently in descending order, sort in ascending order
                val filtered = viewModel.mangaList.value?.sortedBy { it.lastAdded}
                favoriteMangaAdapter.updateList(filtered ?: listOf())
                time.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_upward, 0)
            }
            bottomSheetDialog.dismiss()
        }

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val filteredMangas = viewModel.mangaList.value?.filter { manga ->
                    manga.mangaTitle.contains(s.toString(), ignoreCase = true)
                }

                // Update your adapter with the filtered list
                favoriteMangaAdapter.updateList(filteredMangas ?: listOf())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        rv = view.findViewById(R.id.libraryRv)
        rv.layoutManager = LinearLayoutManager(requireContext())
        viewModel.mangaList.observe(viewLifecycleOwner, Observer {mangas ->
            favoriteMangaAdapter = FavMangaRvAdapter(mangas,
                onItemClick = { favoriteManga ->
                    val intent = Intent(context, MangaDetailActivity::class.java)
                    intent.putExtra("url",favoriteManga.mangaUrl)
                    intent.putExtra("img",favoriteManga.imageUrl)
                    startActivity(intent)
                },
                onItemButtonClick = { favoriteManga ->
                    // Handle button click here
                    viewModel.viewModelScope.launch {
                        withContext(Dispatchers.IO){
                            INSTANCE.favoriteMangaDAO().deleteFavoriteManga(favoriteManga)
                        }
                    }
                    viewModel.getMangas(requireContext())
                }
            )
            rv.adapter = favoriteMangaAdapter
        })

        viewModel.getMangas(requireContext())

    }

    override fun onResume() {
        super.onResume()
        // Fetch the latest data from the database
        viewModel.getMangas(requireContext())
    }

    private fun saveSwitchState(state: Boolean) {
        val sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("autoUpdateSwitch", state)
        editor.apply()
    }
    private suspend fun loadSwitchState(): Boolean = withContext(Dispatchers.IO) {
        val sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE)
        sharedPreferences.getBoolean("autoUpdateSwitch", false)
    }
    private fun scheduleJobService() {
        val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo = JobInfo.Builder(1, ComponentName(requireContext(), CheckNewChaptersJobService::class.java))
            .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY) // Run this job when any network is available
            .setPersisted(true) // Persist this job across device reboots
            .build()

        jobScheduler.schedule(jobInfo)
    }

    private fun cancelJobService() {
        val jobScheduler = context?.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        jobScheduler.cancel(1)
    }
}