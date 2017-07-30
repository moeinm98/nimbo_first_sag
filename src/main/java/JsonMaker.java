import org.json.JSONObject;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;

public class JsonMaker implements Serializable{
    private ConcurrentHashMap<String, Info> userInfoMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Info> repoInfoMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> languageMap = new ConcurrentHashMap<>();

    public void parseJson(String jsonString){
        JSONObject jsonObject = new JSONObject(jsonString);

        updateUserInfo(jsonObject);
        updateRepoInfo(jsonObject);

    }

    private void updateRepoInfo(JSONObject jsonObject) {
        String repoName = jsonObject.getJSONObject("repo").getString("name");

        Info repoInfo = repoInfoMap.get(repoName);
        if (repoInfo == null)
        {
            repoInfo = new RepoInfo(repoName);
            repoInfoMap.put(repoName, repoInfo);
        }

        updateInfoFields(repoInfo, jsonObject);
    }

    private void updateUserInfo(JSONObject jsonObject) {
        String username = jsonObject.getJSONObject("actor").getString("login");

        Info userInfo = userInfoMap.get(username);
        if (userInfo == null){
            userInfo = new UserInfo(username);
            userInfoMap.put(username, userInfo);
        }

        updateInfoFields(userInfo, jsonObject);
    }

    private void updateInfoFields(Info info, JSONObject jsonObject) {
        String type = jsonObject.getString("type");
        switch (type){
            case "CommitCommentEvent":
                info.setCommitCommentNum(info.getCommitCommentNum() + 1);
                break;
            case "CreateEvent":
                info.setCreationNum(info.getCreationNum() + 1);
                break;
            case "ForkEvent":
                info.setForkNum(info.getForkNum() + 1);
                break;
            case "IssueCommentEvent":
                info.setIssueCommentNum(info.getIssueCommentNum() + 1);
                break;
            case "IssueEvent":
                info.setIssueNum(info.getIssueNum() + 1);
                break;
            case "LabelEvent":
                info.setLabelNum(info.getLabelNum() + 1);
                break;
            case "MilestoneEvent":
                info.setMilestoneNum(info.getMilestoneNum() + 1);
                break;
            case "PageBuildEvent":
                info.setPageBuildNum(info.getPageBuildNum() + 1);
                break;
            case "PullRequestEvent":
                info.setPullRequestNum(info.getPullRequestNum() + 1);
                extractLanguage(jsonObject);
                break;
            case "PushEvent":
                int commitNum = jsonObject.getJSONObject("payload").getJSONArray("commits").length();
                info.setCommitNum(commitNum + info.getCommitNum());
                break;
            case "ReleaseEvent":
                info.setReleaseNum(info.getReleaseNum() + 1);
                break;
            case "GollumEvent":
                info.setGollumNum(info.getGollumNum() + 1);
                break;
            case "WatchEvent":
                info.setWatchNum(info.getWatchNum() + 1);
                break;
        }
    }

    private void extractLanguage(JSONObject jsonObject) {
        JSONObject pullReqJson = jsonObject.getJSONObject("payload").getJSONObject("pull_request");
        JSONObject repoJson = pullReqJson.getJSONObject("head").getJSONObject("repo");
        String language = repoJson.getString("language");

        if (!languageMap.containsKey(language))
        {
            languageMap.put(language, 1);
        } else
        {
            languageMap.put(language, languageMap.get(language) + 1);
        }
    }

    public ConcurrentHashMap<String, Info> getUserInfoMap() {
        return userInfoMap;
    }

    public void setUserInfoMap(ConcurrentHashMap<String, Info> userInfoMap) {
        this.userInfoMap = userInfoMap;
    }

    public ConcurrentHashMap<String, Info> getRepoInfoMap() {
        return repoInfoMap;
    }

    public void setRepoInfoMap(ConcurrentHashMap<String, Info> repoInfoMap) {
        this.repoInfoMap = repoInfoMap;
    }
}
