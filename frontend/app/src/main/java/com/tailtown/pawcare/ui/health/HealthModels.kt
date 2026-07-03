package com.tailtown.pawcare.ui.health

data class VaccineRecord(
    val id: String,
    val name: String,
    val frequency: String,           // "Annual"
    val status: VaccineStatus,
    val dueDateLabel: String? = null,    // "Wed, 22 Jun"
    val givenDateLabel: String? = null,  // "12 Apr"
    val vet: String? = null,
)

enum class VaccineStatus { DUE, UPCOMING, COMPLETED }

data class TimelineEntry(
    val id: String,
    val type: TimelineEntryType,
    val title: String,
    val subtitle: String,
    val dateLabel: String,
    val note: String? = null,
)

enum class TimelineEntryType { CHECK_UP, PRESCRIPTION, VACCINE }

data class TimelineGroup(val monthLabel: String, val entries: List<TimelineEntry>)

data class DoseSlot(val time: String, val taken: Boolean)

data class PrescriptionRecord(
    val id: String,
    val name: String,
    val reason: String,
    val daysLeft: Int,
    val dosage: String,
    val frequency: String,
    val duration: String,
    val prescribedBy: String,
    val refillDaysLeft: Int,
    val doses: List<DoseSlot>,
)

data class WeightPoint(val monthLabel: String, val value: Float)

val sampleVaccineRecords = listOf(
    VaccineRecord(
        id = "vax-dhpp",
        name = "DHPP booster",
        frequency = "Annual",
        status = VaccineStatus.DUE,
        dueDateLabel = "Wed, 22 Jun",
    ),
    VaccineRecord(
        id = "vax-rabies",
        name = "Rabies",
        frequency = "Annual",
        status = VaccineStatus.UPCOMING,
        dueDateLabel = "14 Nov",
    ),
    VaccineRecord(
        id = "vax-lepto",
        name = "Leptospirosis",
        frequency = "Annual",
        status = VaccineStatus.COMPLETED,
        givenDateLabel = "12 Apr",
        vet = "Dr. Mehta",
    ),
    VaccineRecord(
        id = "vax-kennel",
        name = "Kennel cough",
        frequency = "Annual",
        status = VaccineStatus.COMPLETED,
        givenDateLabel = "03 Mar",
        vet = "Dr. Mehta",
    ),
)

val sampleTimelineGroups = listOf(
    TimelineGroup(
        monthLabel = "JUNE 2026",
        entries = listOf(
            TimelineEntry(
                id = "cu1",
                type = TimelineEntryType.CHECK_UP,
                title = "Check-up",
                subtitle = "Dr. Mehta · Indirapuram",
                dateLabel = "14 Jun",
                note = "Healthy weight. Dental cleaning recommended in 3 mo.",
            ),
            TimelineEntry(
                id = "rx1",
                type = TimelineEntryType.PRESCRIPTION,
                title = "Prescription",
                subtitle = "Apoquel 16mg · 14 days",
                dateLabel = "14 Jun",
            ),
        ),
    ),
    TimelineGroup(
        monthLabel = "APRIL 2026",
        entries = listOf(
            TimelineEntry(
                id = "vax1",
                type = TimelineEntryType.VACCINE,
                title = "Leptospirosis",
                subtitle = "Annual booster",
                dateLabel = "12 Apr",
            ),
        ),
    ),
)

val samplePrescription = PrescriptionRecord(
    id = "rx1",
    name = "Apoquel 16mg",
    reason = "For Bruno's seasonal allergies",
    daysLeft = 9,
    dosage = "1 tablet · with food",
    frequency = "Twice a day",
    duration = "14 days",
    prescribedBy = "Dr. Mehta · 14 Jun",
    refillDaysLeft = 9,
    doses = listOf(
        DoseSlot(time = "8 AM", taken = true),
        DoseSlot(time = "8 PM", taken = false),
    ),
)

val sampleWeightPoints = listOf(
    WeightPoint(monthLabel = "Jan", value = 27.6f),
    WeightPoint(monthLabel = "Feb", value = 27.8f),
    WeightPoint(monthLabel = "Mar", value = 28.0f),
    WeightPoint(monthLabel = "Apr", value = 28.0f),
    WeightPoint(monthLabel = "May", value = 28.1f),
    WeightPoint(monthLabel = "Jun", value = 28.4f),
)
