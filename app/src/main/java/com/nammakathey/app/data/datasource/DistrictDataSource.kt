package com.nammakathey.app.data.datasource

import com.nammakathey.app.R
import com.nammakathey.app.data.model.District

object DistrictDataSource {
    val districts = listOf(
        District("d1", "Bagalkot", "ಬಾಗಲಕೋಟೆ", R.drawable.dist_bagalkot),
        District("d2", "Ballari", "ಬಳ್ಳಾರಿ", R.drawable.dist_ballari),
        District("d3", "Belagavi", "ಬೆಳಗಾವಿ", R.drawable.dist_belagavi),
        District("d5", "Bengaluru Urban", "ಬೆಂಗಳೂರು ನಗರ", R.drawable.dist_bangaluru),
        District("d8", "Chikkaballapur", "ಚಿಕ್ಕಬಳ್ಳಾಪುರ", R.drawable.dist_chikkaballapur),
        District("d11", "Dakshina Kannada", "ದಕ್ಷಿಣ ಕನ್ನಡ", R.drawable.dist_dakshina_kannada),
        District("d13", "Dharwad", "ಧಾರವಾಡ", R.drawable.dist_dharwad),
        District("d18", "Kodagu", "ಕೊಡಗು", R.drawable.dist_kodagu),
        District("d21", "Mandya", "ಮಂಡ್ಯ", R.drawable.dist_mandya),
        District("d22", "Mysuru", "ಮೈಸೂರು", R.drawable.dist_mysore),
        District("d25", "Shivamogga", "ಶಿವಮೊಗ್ಗ", R.drawable.dist_shivamogga),
        District("d29", "Vijayapura", "ವಿಜಯಪುರ", R.drawable.dist_vijaypura)
    )

    fun getAllDistricts(): List<District> = districts

    fun getDistrictById(id: String): District? = districts.find { it.id == id }
}
