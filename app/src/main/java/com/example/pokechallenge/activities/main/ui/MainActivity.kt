package com.example.pokechallenge.activities.main.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.pokechallenge.PokemonUISDKInterface
import com.example.pokechallenge.activities.main.logic.MainActivityViewModel
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.ErrorStatus
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.PokemonDisplayed.None
import com.example.pokechallenge.activities.main.logic.MainActivityViewState.PokemonDisplayed.Pokemon
import com.example.pokechallenge.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.jakewharton.rxbinding4.view.clicks
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private lateinit var binding: ActivityMainBinding

    private var uiDisposable: Disposable? = null

    @Inject
    lateinit var pokemonSDK: PokemonUISDKInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onStart() {
        super.onStart()

        Log.d("DEBUG", "Subscribe observables")
        uiDisposable = viewModel.attachObservables(
            binding.btnSearch.clicks(),
            binding.editSearch.textChanges().skipInitialValue()
        )
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {

                Log.d("DEBUG", "New state received -> $it")

                binding.btnSearch.isEnabled = it.isButtonEnabled

                if (it.showLoading) {
                    binding.progress.visibility = View.VISIBLE
                    binding.btnSearch.visibility = View.INVISIBLE
                } else {
                    binding.progress.visibility = View.GONE
                    binding.btnSearch.visibility = View.VISIBLE
                }

                when (it.pokemonDisplayed) {
                    None -> binding.scrollContainer.removeAllViews()
                    is Pokemon -> {
                        binding.scrollContainer.removeAllViews()

                        binding.scrollContainer.addView(
                            pokemonSDK.createLayoutFrom(
                                this,
                                it.pokemonDisplayed.imageURL,
                                it.pokemonDisplayed.description
                            )
                        )
                    }
                }

                when (it.errorStatus) {
                    is ErrorStatus.Show ->
                        Snackbar.make(
                            findViewById(android.R.id.content),
                            it.errorStatus.message,
                            Snackbar.LENGTH_SHORT
                        )
                            .show()

                    ErrorStatus.None -> Unit
                }
            }
    }

    override fun onStop() {
        Log.d("DEBUG", "Dispose observables")
        uiDisposable?.dispose()
        super.onStop()
    }
}