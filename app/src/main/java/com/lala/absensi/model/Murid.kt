package com.lala.absensi

data class Murid(
    val nis: String?,
    val email: String?,
    val password: String?,
    val nama: String?,
    val jenisKelamin: String?,
    val jurusan: String?,
    val dataKehadiran: List<Kehadiran>?
)

data class Kehadiran(
    val kelurahan: String?,
    val kecamatan: String?,
    val kota: String?,
    val jamMasuk: String?,
    val jamPulang: String?
)
