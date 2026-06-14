package com.example.pharmacyinventory

import com.example.pharmacyinventory.data.CartLine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SaleDraftStore {
    private val _cartLines = MutableStateFlow<List<CartLine>>(emptyList())
    val cartLines: StateFlow<List<CartLine>> = _cartLines.asStateFlow()

    private val _lastSaleId = MutableStateFlow<Long?>(null)
    val lastSaleId: StateFlow<Long?> = _lastSaleId.asStateFlow()

    fun add(line: CartLine) {
        _cartLines.value = (_cartLines.value + line)
            .groupBy { it.medicineId }
            .map { (_, lines) ->
                val first = lines.first()
                first.copy(
                    requestedQuantity = lines.sumOf { it.requestedQuantity },
                    mrp = lines.first().mrp
                )
            }
    }

    fun remove(medicineId: Long) {
        _cartLines.value = _cartLines.value.filterNot { it.medicineId == medicineId }
    }

    fun clear() {
        _cartLines.value = emptyList()
    }

    fun setLastSaleId(saleId: Long) {
        _lastSaleId.value = saleId
    }
}
