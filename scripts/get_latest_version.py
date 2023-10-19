import os
import requests


def load_properties(filepath, sep='=', comment_char='#'):
    """
    Read the file passed as parameter as a properties file.
    """
    props = {}
    with open(filepath, "rt") as f:
        for line in f:
            line = line.strip()
            if line and not line.startswith(comment_char):
                key_value = line.split(sep)
                key = key_value[0].strip()
                value = sep.join(key_value[1:]).strip().strip('"') 
                props[key] = value 
    return props


# First obtain the path of the version.properties file
script_path = os.path.realpath(__file__)
script_dir = os.path.dirname(script_path)
version_properties = os.path.join(script_dir, "..", "version.properties")
# First get the version stored locally
properties = load_properties(version_properties)

local_android_version_name = properties["android.versionName"]
local_android_version_split = local_android_version_name.split(".")
local_android_version = {
    "major": int(local_android_version_split[0]),
    "minor": int(local_android_version_split[1]),
    "patch": int(local_android_version_split[2])
}

gh_token = os.environ["GITHUB_TOKEN"]
url = "https://api.github.com/repos/FilaMagenta/App/releases?page=1&per_page=1"
request = requests.get(
    url=url,
    headers={
        "Authorization": f"token {gh_token}",
        "Accept": "application/vnd.github+json",
        "X-GitHub-Api-Version": "2022-11-28"
    },
    params={
        "page": "1",
        "per_page": "1"
    }
)
data = request.json()

version: dict = data[0]
tag: str = version["tag_name"]
version_pieces = tag.split("-")
android_version = version_pieces[1]
split_android_version = android_version.split(".")

remote_android_version = {
    "major": int(split_android_version[0]),
    "minor": int(split_android_version[1]),
    "patch": int(split_android_version[2])
}

if remote_android_version["major"] > local_android_version["major"]:
    print("5")
elif remote_android_version["minor"] > local_android_version["minor"]:
    print("3")
else:
    print("1")
