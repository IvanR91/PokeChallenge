package com.example.pokechallenge.activities

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.example.pokechallenge.activities.MainActivityViewModel.SearchState.Clear
import com.example.pokechallenge.activities.MainActivityViewModel.SearchState.Loaded
import com.example.pokechallenge.PokemonUISDKInterface
import com.example.pokechallenge.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private var uiDisposable: Disposable? = null
    private var errorDisposable: Disposable? = null

    @Inject
    lateinit var pokemonSDK: PokemonUISDKInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.searchView.setIconifiedByDefault(false)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                viewModel.clickSearchButton(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()

        uiDisposable = viewModel.observableUI.observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                binding.progress.visibility =
                    if (it.showLoading) View.VISIBLE
                    else View.GONE

                when (it.searchState) {
                    is Loaded -> {
                        binding.scrollContainer.removeAllViews()

                        binding.scrollContainer.addView(
                            pokemonSDK.createLayoutFrom(
                                this,
                                it.searchState.sprite,
                                it.searchState.description
                            )
                        )
                    }

                    Clear -> binding.scrollContainer.removeAllViews()
                }
            }

        errorDisposable = viewModel.observableError.observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                Snackbar.make(findViewById(android.R.id.content), it, Snackbar.LENGTH_SHORT).show()
            }
    }

    override fun onStop() {
        uiDisposable?.dispose()
        errorDisposable?.dispose()
        super.onStop()
    }
}