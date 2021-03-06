package ir.sahab.nimbo.githubTrends;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class Statistics implements Serializable {
    private ConcurrentHashMap<String, Info> userInfoMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Info> repoInfoMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> languageMap = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> organizationMap = new ConcurrentHashMap<>();
    private String finalUpdateTime;

    public void updateStatistics(String jsonString) {
        JSONObject jsonObject = new JSONObject(jsonString);

        String creationDate = jsonObject.getString("created_at");
        finalUpdateTime = creationDate.substring(creationDate.indexOf('T') + 1, creationDate.indexOf('Z'));

        updateUserInfo(jsonObject);
        updateRepoInfo(jsonObject);
    }

    private void updateRepoInfo(JSONObject jsonObject) {
        String repoName = jsonObject.getJSONObject("repo").getString("name");

        Info repoInfo = repoInfoMap.get(repoName);
        if (repoInfo == null) {
            repoInfo = new RepoInfo(repoName);
            repoInfoMap.put(repoName, repoInfo);
        }

        updateInfoFields(repoInfo, jsonObject);
    }

    private void updateUserInfo(JSONObject jsonObject) {
        String username = jsonObject.getJSONObject("actor").getString("login");

        Info userInfo = userInfoMap.get(username);
        if (userInfo == null) {
            userInfo = new UserInfo(username);
            userInfoMap.put(username, userInfo);
        }

        updateInfoFields(userInfo, jsonObject);
    }

    private void updateInfoFields(Info info, JSONObject jsonObject) {
        extractOrganization(jsonObject);
        String type = jsonObject.getString("type");
        switch (type) {
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
                int commitNum = jsonObject.getJSONObject("payload").getJSONArray("commits").length(); //. notcommented _ commented
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

    private void extractOrganization(JSONObject jsonObject) {
        try {
            JSONObject orgObject = jsonObject.getJSONObject("org");
            if (orgObject != null) {
                String organizationName = orgObject.getString("login");
                if (!organizationMap.containsKey(organizationName)) {
                    organizationMap.put(organizationName, 1);
                } else {
                    organizationMap.put(organizationName, organizationMap.get(organizationName) + 1);
                }
            }
        } catch (JSONException ignored) {
        }
    }

    private void extractLanguage(JSONObject jsonObject) {
        JSONObject pullReqJson = jsonObject.getJSONObject("payload").getJSONObject("pull_request");
        JSONObject repoJson = pullReqJson.getJSONObject("head").getJSONObject("repo");
        String language = repoJson.getString("language");

        if (!languageMap.containsKey(language)) {
            languageMap.put(language, 1);
        } else {
            languageMap.put(language, languageMap.get(language) + 1);
        }
    }

    public ConcurrentHashMap<String, Integer> getOrganizationMap() {
        return organizationMap;
    }

    public void setOrganizationMap(ConcurrentHashMap<String, Integer> organizationMap) {
        this.organizationMap = organizationMap;
    }

    public void mergeStatistics(Statistics statistics) {
        ConcurrentHashMap<String, Info> userInfoMap = statistics.getUserInfoMap();
        ConcurrentHashMap<String, Info> repoInfoMap = statistics.getRepoInfoMap();
        ConcurrentHashMap<String, Integer> languageMap = statistics.getLanguageMap();
        ConcurrentHashMap<String, Integer> organizationMap = statistics.getOrganizationMap();

        mergeInfoMap(userInfoMap, userInfoMap);
        mergeInfoMap(repoInfoMap, repoInfoMap);
        mergeIntegerMap(languageMap);
        mergeIntegerMap(organizationMap);
    }

    private void mergeIntegerMap(ConcurrentHashMap<String, Integer> languageMap) {
        for (String name : languageMap.keySet()) {
            int languageRepeatedInThisHourOrday = 0;
            int languageRepeatedInTenMinsOrHour;
            if (this.languageMap.containsKey(name)) {
                languageRepeatedInThisHourOrday = this.languageMap.get(name);
            }
            languageRepeatedInTenMinsOrHour = languageMap.get(name);
            this.languageMap.put(name, languageRepeatedInTenMinsOrHour + languageRepeatedInThisHourOrday);
        }
    }

    private void mergeInfoMap(ConcurrentHashMap<String, Info> receivedMap, ConcurrentHashMap<String, Info> mainMap) {
        for (String infoName : receivedMap.keySet()) {
            Info info = receivedMap.get(infoName);

            if (mainMap.containsKey(infoName)) {
                info.mergeInfo(mainMap.get(infoName));
            } else {
                mainMap.put(infoName, info);
            }
        }
    }

    public void clearStatistics() {
        userInfoMap.clear();
        repoInfoMap.clear();
        languageMap.clear();
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

    public ConcurrentHashMap<String, Integer> getLanguageMap() {
        return languageMap;
    }

    public void setLanguageMap(ConcurrentHashMap<String, Integer> languageMap) {
        this.languageMap = languageMap;
    }

    public Object[] findAndGetTrends() {
        Object[] trends = new Object[4]; //trends[0]:username //trends[1]:repoName //trends[2]:language
        trends[0] = findInfoTrend(userInfoMap);
        trends[1] = findInfoTrend(repoInfoMap);
        trends[2] = findIntegerTrend(languageMap);
        trends[3] = findIntegerTrend(organizationMap);
        return trends;
    }

    private String findIntegerTrend(ConcurrentHashMap<String, Integer> languageMap) {
        Optional<Map.Entry<String,Integer>> optional=languageMap.entrySet().stream().parallel().max(Comparator.comparing(Map.Entry::getValue));
        if (optional.isPresent())
            return optional.get().getKey();
         return "null";
    }

    private Info findInfoTrend(ConcurrentHashMap<String, Info> infoMap) {
        Optional<Map.Entry<String,Info>> optional= infoMap.entrySet().stream().parallel().max(Comparator.comparing(Map.Entry::getValue));
        if (optional.isPresent())
            return optional.get().getValue();
        return new UserInfo("null");
    }

    public String getFinalUpdateTime() {
        return finalUpdateTime;
    }

    public void setFinalUpdateTime(String finalUpdateTime) {
        this.finalUpdateTime = finalUpdateTime;
    }
}
