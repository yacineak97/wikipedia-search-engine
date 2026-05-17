def progress_bar(current, total, label="Progress"):
    if current > total:
        return

    percentage = (current / total) * 100

    bar_length = 30
    filled = int(bar_length * current // total)

    bar = "█" * filled + "-" * (bar_length - filled)

    print(
        f"\r[{label}] [{bar}] {percentage:.2f}%",
        end="" if current < total else "\n"
    )