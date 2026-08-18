# Any function related to sentiment display or processing

def sentiment_label(score):
    """Convert compound score to label and color."""
    if score is None:
        return ("N/A", "secondary")
    if score > 0.05:
        return ("Positive", "success")
    elif score < -0.05:
        return ("Negative", "danger")
    else:
        return ("Neutral", "secondary")
