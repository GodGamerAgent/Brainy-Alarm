package com.example.data

enum class ChallengeType(
    val title: String,
    val description: String,
    val iconName: String
) {
    MATH("Math Problems", "Solve arithmetic calculation equations", "calculate"),
    MEMORY("Memory Match", "Find matching pairs of hidden icons", "grid_view"),
    SEQUENCE("Tile Sequence", "Tap numbered tiles in chronological order", "format_list_numbered"),
    SIMON("Simon Says", "Remember and repeat the flashing color sequence", "gamepad"),
    SHAKE("Shake Phone", "Vigorously shake your phone until fully awake", "vibration"),
    TYPING("Text Rewrite", "Accurately type challenging morning phrases", "keyboard"),
    STROOP("Color Match (Stroop)", "Select ink colors under cognitive distraction", "palette"),
    BARCODE("Barcode / QR Scan", "Scan barcode in bathroom, kitchen, or desk", "qr_code_scanner")
}
