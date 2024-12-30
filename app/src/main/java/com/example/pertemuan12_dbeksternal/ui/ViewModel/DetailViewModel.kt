package com.example.pertemuan12_dbeksternal.ui.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.network.HttpException
import com.example.pertemuan12_dbeksternal.model.Mahasiswa
import com.example.pertemuan12_dbeksternal.repository.MahasiswaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

sealed class DetailUiState {
    data class Success(val mahasiswa: Mahasiswa) : DetailUiState()
    object Error : DetailUiState()
    object Loading : DetailUiState()
}

class DetailViewModel(private val repository: MahasiswaRepository) : ViewModel() {
    private val _detailUiState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val detailUiState: StateFlow<DetailUiState> = _detailUiState

    fun getMahasiswaById(nim: String) {
        viewModelScope.launch {
            _detailUiState.value = DetailUiState.Loading
            try {
                val mahasiswa = repository.getMahasiswaById(nim)
                _detailUiState.value = DetailUiState.Success(mahasiswa)
            } catch (e: IOException) {
                e.printStackTrace()
                _detailUiState.value = DetailUiState.Error
            } catch (e: HttpException) {
                e.printStackTrace()
                _detailUiState.value = DetailUiState.Error
            }
        }
    }
}