import os
import glob
from git import Repo
from github import Github
from smell import SmellDetectionAndRefactoring
from github.GithubException import GithubException
from datetime import datetime
import shutil
from metrics import calculate_code_quality_metrics, compare_code_quality

GITHUB_TOKEN = os.environ.get("GITHUB_TOKEN")
GITHUB_REPO_URL = "https://github.com/alext04/se1_3d_test.git"
LOCAL_REPO_PATH = "test_repo"
TARGET_BRANCH = "main"
NEW_BRANCH = "automated-refactor"
TIMESTAMP = datetime.now().strftime('%Y%m%d_%H%M%S')

def clone_or_update_repo():
    print("[INFO] Setting up repository...")
    if os.path.exists(LOCAL_REPO_PATH):
        print("[INFO] Removing existing repository...")
        shutil.rmtree(LOCAL_REPO_PATH)
    
    # Cloning
    print("[INFO] Cloning fresh copy of repository...")
    Repo.clone_from(GITHUB_REPO_URL, LOCAL_REPO_PATH)
    print("[SUCCESS] Repository cloned successfully")

def create_new_branch():
    global NEW_BRANCH  
    repo = Repo(LOCAL_REPO_PATH)
    NEW_BRANCH = f"{NEW_BRANCH}_{TIMESTAMP}"

    if NEW_BRANCH in [h.name for h in repo.heads]:
        repo.git.checkout(NEW_BRANCH)
        repo.git.reset('--hard', f'origin/{TARGET_BRANCH}')
    else:
        base = repo.heads[TARGET_BRANCH]
        repo.create_head(NEW_BRANCH, base.commit)
        repo.git.checkout(NEW_BRANCH)

    print(f"[INFO] Switched to new branch: {NEW_BRANCH}")

def get_java_files(directory):
    return glob.glob(os.path.join(directory, "**/*.java"), recursive=True)

def detect_and_refactor():
    print("\n[INFO] Starting smell detection and refactoring pipeline...")
    
    tool = SmellDetectionAndRefactoring()
    file_paths = get_java_files(LOCAL_REPO_PATH)
    
    # file_paths = file_paths[:2]
    print(f"[INFO] Found {len(file_paths)} Java files to analyze")
    deepseek=0
    gemini=0
    for file_path in file_paths:
        print(f"\n[INFO] Processing file: {file_path}")
        with open(file_path, "r", encoding="utf-8") as f:
            content = f.read()

        initial_quality = calculate_code_quality_metrics(content)

        code_smells_report_gemini = tool.detect_code_smells(content, api="gemini")
        design_smells_report_gemini = tool.detect_design_smells(content, api="gemini")
        code_smells_report_deepseek = tool.detect_code_smells(content, api="deepseek")
        design_smells_report_deepseek = tool.detect_design_smells(content, api="deepseek")

        # Print identified smells
        print("\n=== Identified Smells (Gemini) ===")
        print("\nCode Smells:")
        print("-" * 50)
        print(code_smells_report_gemini if code_smells_report_gemini.strip() else "None detected")
        print("\nDesign Smells:")
        print("-" * 50)
        print(design_smells_report_gemini if design_smells_report_gemini.strip() else "None detected")
        print("-" * 50)

        print("\n=== Identified Smells (DeepSeek) ===")
        print("\nCode Smells:")
        print("-" * 50)
        print(code_smells_report_deepseek if code_smells_report_deepseek.strip() else "None detected")
        print("\nDesign Smells:")
        print("-" * 50)
        print(design_smells_report_deepseek if design_smells_report_deepseek.strip() else "None detected")
        print("-" * 50)

        refactored_code_gemini = content
        refactored_code_deepseek = content

        if (not code_smells_report_gemini.strip()) and (not design_smells_report_gemini.strip()):
            print("[INFO] No smells detected in this file by Gemini, skipping...")
        else:
            print("[INFO] Smells detected by Gemini, proceeding with refactoring...")
            refactored_code_gemini = tool.refactor_code(content, code_smells_report_gemini, design_smells_report_gemini, api="gemini")

        if (not code_smells_report_deepseek.strip()) and (not design_smells_report_deepseek.strip()):
            print("[INFO] No smells detected in this file by DeepSeek, skipping...")
        else:
            print("[INFO] Smells detected by DeepSeek, proceeding with refactoring...")
            refactored_code_deepseek = tool.refactor_code(content, code_smells_report_deepseek, design_smells_report_deepseek, api="deepseek")

        better_refactored_code = compare_code_quality(content, refactored_code_gemini, refactored_code_deepseek)

        if better_refactored_code == "gemini":
            with open(file_path, "w", encoding="utf-8") as f:
                f.write(refactored_code_gemini)
            gemini+=1
            print(f"[SUCCESS] Successfully refactored {file_path} using Gemini")
        else:
            with open(file_path, "w", encoding="utf-8") as f:
                f.write(refactored_code_deepseek)
            deepseek+=1
            print(f"[SUCCESS] Successfully refactored {file_path} using DeepSeek")
    
    print("\n[SUCCESS] Completed smell detection and refactoring pipeline")
    print(f"[INFO] Refactored {gemini} files using Gemini and {deepseek} files using DeepSeek")

def commit_and_push_changes():
    repo = Repo(LOCAL_REPO_PATH)
    repo.git.add('--all')
    current_time = datetime.now().strftime('%Y-%m-%d %H:%M:%S')
    commit_message = f"Automated code & design smell refactoring - {current_time}"
    print(commit_message)
    try:
        repo.git.commit('-m', commit_message)
    except Exception as e:
        print(f"[INFO] No changes to commit: {e}")
    origin = repo.remotes.origin
    origin.push(NEW_BRANCH)

def create_pull_request():
    if not GITHUB_TOKEN:
        raise ValueError("GitHub token is missing. Set it as an environment variable: GITHUB_TOKEN.")

    repo_name = GITHUB_REPO_URL.split("github.com/")[1]
    if repo_name.endswith(".git"):
        repo_name = repo_name[:-4]

    g = Github(GITHUB_TOKEN)
    repo = g.get_repo(repo_name)
    
    # Verifing branch creation
    try:
        repo.get_branch(NEW_BRANCH)
        print(f"Branch '{NEW_BRANCH}' exists on remote.")
    except GithubException as e:
        if e.status == 404:
            raise Exception(f"Branch '{NEW_BRANCH}' does not exist on the remote.") from e
        raise
    
    print("Checking for existing pull requests...")
    prs = repo.get_pulls(state="open", head=f"{repo.owner.login}:{NEW_BRANCH}")
    if prs.totalCount > 0:
        print(f"A pull request already exists: {prs[0].html_url}")
        return

    try:
        title = f"Automated Refactor PR - {TIMESTAMP}"
        body = (
            f"This PR contains automated refactored changes made on {TIMESTAMP}.\n\n"
        )
        pr = repo.create_pull(title=title, body=body, head=NEW_BRANCH, base=TARGET_BRANCH)
        print(f"Pull request created successfully: {pr.html_url}")
    except GithubException as e:
        print(f"Failed to create pull request: {e.data}")
        raise

def delete_local_repo():
    try:
        if os.path.exists(LOCAL_REPO_PATH):
            print(f"[INFO] Deleting local repository at: {LOCAL_REPO_PATH}...")
            shutil.rmtree(LOCAL_REPO_PATH)
            print("[SUCCESS] Local repository deleted successfully.")
        else:
            print("[INFO] No local repository found to delete.")
    except Exception as e:
        print(f"[ERROR] Failed to delete local repository: {e}")

print("Running pipeline...")
clone_or_update_repo()
create_new_branch()
detect_and_refactor()
commit_and_push_changes()
create_pull_request()
delete_local_repo()