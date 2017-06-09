package demo.functions.view;

import demo.functions.project.*;
import org.junit.Assert;
import org.junit.Test;

import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

public class ViewProcessorTest {

    @Test
    public void generateView() throws Exception {

        MessageDigest md5 = java.security.MessageDigest.getInstance("MD5");

        Commit commit = new Commit(Arrays.asList(new File("file1.txt"),
                new File("file2.txt")));

        Project project = new Project("test");
        project.setIdentity(1L);
        project.setOwner("test");
        project.setStatus(ProjectStatus.PROJECT_CREATED);

        ProjectEvent projectEvent = new ProjectEvent();
        Map<String, Commit> map = new HashMap<>();
        map.put("commit", commit);
        projectEvent.setPayload(map);
        projectEvent.setProjectId(project.getIdentity());
        projectEvent.setType(ProjectEventType.COMMIT_EVENT);

        ViewProcessor viewProcessor = new ViewProcessor(projectEvent, project, commit);

        View expectedView = new View("tcq");

        expectedView.setFileIds(commit.getFiles().stream()
                .map(f -> f.getFileName().toLowerCase())
                .sorted()
                .collect(Collectors.toList()));

        expectedView.setMatches(1);
        expectedView.setProjectId(project.getIdentity());
        expectedView.setId("tcq_1_" + viewProcessor.getMd5Hash(expectedView.getFileIds().stream()
                .map(viewProcessor::getMd5Hash)
                .collect(Collectors.joining("_"))));

        List<View> expectedViewList = Collections.singletonList(expectedView);
        List<View> actualViewList = viewProcessor.generateView();

        System.out.println(expectedViewList.toString());
        System.out.println(actualViewList.toString());

        Assert.assertArrayEquals(expectedViewList.toArray(), actualViewList.toArray());
    }

    @Test
    public void subsetReturnsCorrectCombinations() {
        ViewProcessor viewProcessor = new ViewProcessor();

        List<String> input = Arrays.asList("a", "b", "c");
        List<List<String>> expected = Arrays.asList(Arrays.asList("a", "b"),
                Arrays.asList("a", "c"),
                Arrays.asList("b", "c"));

        List<List<String>> actual = viewProcessor.subsets(input);

        Assert.assertArrayEquals(expected.stream().flatMap(Collection::stream).toArray(),
                actual.stream().flatMap(Collection::stream).toArray());
    }
}