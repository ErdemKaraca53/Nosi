package com.erdem.nosi.request

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.erdem.nosi.data.local.CollectionEntity
import com.erdem.nosi.data.local.CollectionSummary
import com.erdem.nosi.data.local.NosiDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Ana ekran için ViewModel — koleksiyonları ve özetlerini yönetir.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = NosiDatabase.getInstance(application).translationDao()

    /** Koleksiyon özetleri (Flow — her değişiklikte otomatik güncellenir) */
    val collectionSummaries: Flow<List<CollectionSummary>> = dao.getCollectionSummaries()

    init {
        // İlk çalıştırmada varsayılan "My Words" koleksiyonunu oluştur
        viewModelScope.launch {
            if (dao.collectionExistsByName("My Words") == 0) {
                dao.insertCollection(CollectionEntity(name = "My Words"))
            }
        }
    }
}
