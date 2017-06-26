package demo.functions.query;

import demo.functions.project.Commit;
import demo.functions.project.File;
import demo.functions.project.Project;
import demo.functions.project.ProjectEvent;
import demo.functions.view.View;
import org.springframework.util.Assert;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.codec.digest.MessageDigestAlgorithms.MD5;

public class TightCouplingProcessor {

    private ProjectEvent projectEvent;
    private Project project;
    private Commit commit;

    public TightCouplingProcessor() {
    }

    public TightCouplingProcessor(ProjectEvent projectEvent, Project project, Commit commit) {
        this.projectEvent = projectEvent;
        this.project = project;
        this.commit = commit;
    }

    public List<View> generateView() {
        Assert.notNull(project, "Project must not be null");
        Assert.notNull(projectEvent, "Event must not be null");
        Assert.notNull(commit, "Commit must not be null");
        Assert.notNull(commit, "Commit must contain files");

        // Do not generate view if there are no files
        if (commit.getFiles().size() == 0)
            return null;

        List<File> fileList = commit.getFiles();

        // Create coupled change index of unique file pairs
        List<List<String>> fileGroups = subsets(fileList.stream()
                .map(f -> f.getFileName().toLowerCase())
                .sorted()
                .collect(Collectors.toList()));

        // Create a view for each file coupling
        return fileGroups.stream()
                .map(f -> {
                    String key = generateKey(f);
                    View view = new View("tcq");
                    view.setId(key);
                    view.setProjectId(projectEvent.getProjectId());
                    view.setMatches(1);
                    view.setFileIds(f);
                    view.setCreatedAt(commit.getCreatedAt());
                    view.setLastModified(commit.getCommitDate());
                    return view;
                }).collect(Collectors.toList());
    }

    private String generateKey(List<String> files) {
        Assert.notNull(projectEvent, "Project event must not be null");
        Assert.notNull(projectEvent.getProjectId(), "Project event must have a valid project id");
        String keyTemplate = "tcq_%s_%s";

        // Creates a hashed composite key of the coupled file names
        String compositeKey = getMd5Hash(files.stream()
                .map(this::getMd5Hash)
                .collect(Collectors.joining("_")));

        return String.format(keyTemplate, projectEvent.getProjectId(), compositeKey);
    }

    List<List<String>> subsets(List<String> set) {
        List<List<String>> subsets = new ArrayList<>();
        int n = set.size();

        // Get the unique subsets with length equal to 2
        for (int i = 0; i < (1 << n); i++) {
            List<String> combination = new ArrayList<>();
            for (int j = 0; j < n; j++)
                if ((i & (1 << j)) > 0)
                    combination.add(set.get(j));

            // Only collect unique pairs of file names
            if (combination.size() == 2)
                subsets.add(combination);
        }

        return subsets;
    }

    /**
     * Converts an arbitrary message into an MD5 hash and returns it as a UTF-8 encoded string
     *
     * @param message is the message to convert
     * @return a UTF-8 encoded string representation of the MD5 hash
     */
    String getMd5Hash(String message) {
        String result;

        try {
            MessageDigest md5 = MessageDigest.getInstance(MD5);
            result = (new HexBinaryAdapter()).marshal(md5.digest(message.getBytes())).toLowerCase();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error converting message to MD5 hash", e);
        }

        return result;
    }
}
