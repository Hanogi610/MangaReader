package com.example.mangareader.fragment


import adapter.MangaRankingRvAdapter
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangareader.R
import com.example.mangareader.activity.MangaDetailActivity

class MangaRankingsFragment : Fragment() {

    data class StatusOption(val name: String, val value: Int) {
        override fun toString(): String {
            return name
        }
    }

    lateinit var tvNoInternet: TextView
    lateinit var statusSpinner: Spinner
    lateinit var recyclerView: RecyclerView
    lateinit var progressBar: ProgressBar
    private val viewModel: MangaRankingsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manga_rankings, container, false)

        statusSpinner = view.findViewById(R.id.status_spinner)
        recyclerView = view.findViewById(R.id.rv_manga_rankings)
        tvNoInternet = view.findViewById(R.id.tv_no_internet)
        progressBar = view.findViewById(R.id.loading_spinner)

        // Set up your Spinners
        val statusOptions = arrayOf(StatusOption("Weekly", 1), StatusOption("Monthly", 2), StatusOption("All time", 3))

        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        statusSpinner.adapter = statusAdapter
        statusSpinner.setSelection(0)

        // Set up Spinner selection listeners
        statusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                fetchManga()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
        // Set up your RecyclerView here...
        recyclerView.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        viewModel.mangaList.observe(viewLifecycleOwner, Observer { mangaList ->
            progressBar.visibility = View.GONE
            if(mangaList.isEmpty()){
                tvNoInternet.visibility = View.VISIBLE
            }else{
                val adapter = MangaRankingRvAdapter(mangaList){
                    val intent = Intent(context, MangaDetailActivity::class.java)
                    intent.putExtra("url", it.url)
                    intent.putExtra("img", it.imageUrl)
                    startActivity(intent)
                }
                recyclerView.adapter = adapter
            }
        })

        fetchManga()

        return view
    }
    private fun fetchManga() {
        progressBar.visibility = View.VISIBLE
        val status = (statusSpinner.selectedItem as StatusOption).value
        val url = "https://asuratoon.com/"
        if(isNetworkAvailable(requireContext())){
            viewModel.fetchManga(url,status)
        }else{
            progressBar.visibility = View.GONE
            tvNoInternet.visibility = View.VISIBLE
        }
    }
    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}