package com.example.pertemuan12_dbeksternal.ui.View

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pertemuan12_dbeksternal.R
import com.example.pertemuan12_dbeksternal.model.Mahasiswa
import com.example.pertemuan12_dbeksternal.ui.CustomWidget.TopAppBar
import com.example.pertemuan12_dbeksternal.ui.ViewModel.HomeUiState
import com.example.pertemuan12_dbeksternal.ui.ViewModel.HomeViewModel
import com.example.pertemuan12_dbeksternal.ui.ViewModel.PenyediaViewModel
import com.example.pertemuan12_dbeksternal.ui.navigation.DestinasiNavigasi

object DestinasiHome : DestinasiNavigasi {
    override val route = "home"
    override val titleRes = "Home Mahasiswa"
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateToltemEntry: () -> Unit,
    modifier: Modifier = Modifier,
    onDetailClick: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel(factory = PenyediaViewModel.Factory)
){
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val validasiDelete = remember { mutableStateOf(false) }
    val deleteMhs = remember { mutableStateOf<Mahasiswa?>(null) }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = DestinasiHome.titleRes,
                canNavigateBack = false,
                scrollBehavior = scrollBehavior,
                onRefresh = {
                    viewModel.getMhs()
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToltemEntry,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.padding(18.dp)
            ){
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Mahasiswa")
            }
        },
    ) { innerPadding ->
        HomeStatus(
            homeUiState = viewModel.mhsUiState,
            retryAction = { viewModel.getMhs() },
            modifier = Modifier.padding(innerPadding),
            onDetailClick = onDetailClick,
            onDeleteClick = {
                deleteMhs.value = it
                validasiDelete.value = true
            }
        )

        if (validasiDelete.value) {
            AlertDialog(
                onDismissRequest = { validasiDelete.value = false },
                text = { Text("Apakah Anda yakin ingin menghapus data mahasiswa?") },
                confirmButton = {
                    Button(onClick = {
                        deleteMhs.value?.let { mhs ->
                            viewModel.deleteMhs(mhs.nim)
                        }
                        validasiDelete.value = false
                    }) {
                        Text("hapus")
                    }
                },
                dismissButton = {
                    Button(onClick = { validasiDelete.value = false }) {
                        Text("batalkan")
                    }
                }
            )
        }
    }
}

@Composable
fun HomeStatus(
    homeUiState: HomeUiState,
    retryAction: () -> Unit,
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {},
    onDetailClick: (String) -> Unit
){
    when (homeUiState) {
        is HomeUiState.Loading -> OnLoading(modifier = modifier.fillMaxSize())
        is HomeUiState.Success ->
            if(homeUiState.mahasiswa.isEmpty()){
                return Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "tidak ada data mahasiswa")
                }
            }else {
                MhsLayout(
                    mahasiswa = homeUiState.mahasiswa, modifier = modifier.fillMaxWidth(),
                    onDetailClick = {
                        onDetailClick(it.nim)
                    },
                    onDeleteClick = {
                        onDeleteClick(it)
                    }
                )
            }
        is HomeUiState.Error -> OnError(retryAction, modifier = modifier.fillMaxSize())
    }
}

@Composable
fun OnLoading(modifier: Modifier = Modifier) {
    Image(
        modifier = modifier.size(200.dp),
        painter = painterResource(R.drawable.loading),
        contentDescription =  stringResource(R.string.loading)
    )
}

@Composable
fun OnError(retryAction: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Image(
            painter = painterResource(id = R.drawable.error), contentDescription = ""
        )
        Text(text = stringResource(R.string.loading_failed), modifier = Modifier.padding(16.dp))
        Button(onClick = retryAction){
            Text(stringResource(R.string.retry))
        }
    }
}

@Composable
fun MhsLayout(
    mahasiswa: List<Mahasiswa>,
    modifier: Modifier = Modifier,
    onDetailClick: (Mahasiswa) -> Unit,
    onDeleteClick: (Mahasiswa) -> Unit = {}
){
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ){
        items(mahasiswa){ mahasiswa ->
            MhsCard(
                mahasiswa = mahasiswa,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable{ onDetailClick(mahasiswa) },
                onDeleteClick = {
                    onDeleteClick(mahasiswa)
                }
            )
        }
    }
}

@Composable
fun MhsCard(
    mahasiswa: Mahasiswa,
    modifier: Modifier = Modifier,
    onDeleteClick: (Mahasiswa) -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Person, contentDescription = "Nama")
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = mahasiswa.nama,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = { onDeleteClick(mahasiswa) }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete",
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Create, contentDescription = "NIM")
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = mahasiswa.nim,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.AccountCircle, contentDescription = "Jenis Kelamin")
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = mahasiswa.jenisKelamin,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.ThumbUp, contentDescription = "Angkatan")
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = mahasiswa.angkatan,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Edit, contentDescription = "Kelas")
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = mahasiswa.kelas,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.DateRange, contentDescription = "Alamat")
                Spacer(modifier = Modifier.padding(3.dp))
                Text(
                    text = mahasiswa.alamat,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}