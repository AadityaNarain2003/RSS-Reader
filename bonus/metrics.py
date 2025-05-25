def calculate_loc(code):
    return len(code.splitlines())

def calculate_cyclomatic_complexity(code):
    decision_keywords = ["if", "for", "while", "elif", "case", "switch", "try", "catch"]
    complexity = 1 
    for line in code.splitlines():
        if any(keyword in line for keyword in decision_keywords):
            complexity += 1
    return complexity


def calculate_code_quality_metrics(code):
    loc = calculate_loc(code)
    cyclomatic_complexity = calculate_cyclomatic_complexity(code)

    return {
        "loc": loc,
        "cyclomatic_complexity": cyclomatic_complexity,
    }

def compare_code_quality(initial_code, refactored_code_gemini, refactored_code_deepseek):
    initial_quality = calculate_code_quality_metrics(initial_code)
    gemini_quality = calculate_code_quality_metrics(refactored_code_gemini)
    deepseek_quality = calculate_code_quality_metrics(refactored_code_deepseek)

    print("Initial Code Quality:", initial_quality)
    print("Gemini Refactored Code Quality:", gemini_quality)
    print("DeepSeek Refactored Code Quality:", deepseek_quality)

  
    weights = {
        "loc": 0.3,  
        "cyclomatic_complexity": 0.5,  
    }

    def calculate_score(metrics):
        return (
            weights["loc"] * metrics["loc"] +  # Higher LOC increases the score (worse)
            weights["cyclomatic_complexity"] * metrics["cyclomatic_complexity"]  # Higher complexity increases the score (worse)
        )

    # Calculate scores for each version
    initial_score = calculate_score(initial_quality)
    gemini_score = calculate_score(gemini_quality)
    deepseek_score = calculate_score(deepseek_quality)

    print("\nScores:")
    print(f"Initial Code Score: {initial_score}")
    print(f"Gemini Refactored Code Score: {gemini_score}")
    print(f"DeepSeek Refactored Code Score: {deepseek_score}")

    # Determine the best code based on the lowest score
    if gemini_score < deepseek_score and gemini_score < initial_score:
        return "gemini"
    elif deepseek_score < gemini_score and deepseek_score < initial_score:
        return "deepseek"
    else:
        return "initial"
