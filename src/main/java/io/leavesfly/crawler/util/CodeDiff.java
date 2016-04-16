package io.leavesfly.crawler.util;


import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * @author LeavesFly
 */
public class CodeDiff {
    private Set<String> oriCodeFileNameSet = new HashSet<String>();
    private Set<String> nowCodeFileNameSet = new HashSet<String>();


    private String directoryPathFrom;
    private String directoryPathTo;

    public CodeDiff(String directoryPathFrom, String directoryPathTo) {
        this.directoryPathFrom = directoryPathFrom;
        this.directoryPathTo = directoryPathTo;
    }

    public Set<String> getHadDelCodeFileNames() {

        if (oriCodeFileNameSet.size() == 0 || nowCodeFileNameSet.size() == 0) {
            addCodeFileNames();
        }

        Set<String> delCodeFileNameSet = new HashSet<String>();
        for (String codeName : oriCodeFileNameSet) {
            if (!nowCodeFileNameSet.contains(codeName)) {
                delCodeFileNameSet.add(codeName);
            }
        }
        return delCodeFileNameSet;
    }

    public Set<String> getNewAddCodeFileNames() {
        if (oriCodeFileNameSet.size() == 0 || nowCodeFileNameSet.size() == 0) {
            addCodeFileNames();
        }

        Set<String> newAddCodeFileNameSet = new HashSet<String>();
        for (String codeName : nowCodeFileNameSet) {
            if (!oriCodeFileNameSet.contains(codeName)) {
                newAddCodeFileNameSet.add(codeName);
            }
        }
        return newAddCodeFileNameSet;
    }

    private synchronized void addCodeFileNames() {

        doAddCodeFileNames(true);
        doAddCodeFileNames(false);
    }


    private void doAddCodeFileNames(final boolean isFrom) {
        File directoryPath = null;

        if (isFrom) {
            directoryPath = new File(directoryPathFrom);
        } else {
            directoryPath = new File(directoryPathTo);
        }

        visitFile(directoryPath, new AcceptVisitable() {

            public void visit(File file) {
                if (isFrom) {
                    oriCodeFileNameSet.add(file.getName());
                } else {
                    nowCodeFileNameSet.add(file.getName());
                }
            }

            public boolean isAccepted(File file) {
                if (file != null &&
                        (file.getName().endsWith(".java")
                                || file.getName().endsWith(".xml")
                                || file.getName().endsWith(".vm"))) {
                    return true;
                }
                return false;
            }
        });
    }


    private void visitFile(File file, AcceptVisitable acceptVisitable) {

        if (file.isDirectory()) {
            File[] fileArray = file.listFiles();
            for (File tmpFile : fileArray) {
                visitFile(tmpFile, acceptVisitable);
            }
        } else if (file.isFile() && acceptVisitable.isAccepted(file)) {
            acceptVisitable.visit(file);
        }
    }

    /**
     * @author LeavesFly
     */
    private interface AcceptVisitable {

        boolean isAccepted(File file);

        void visit(File file);
    }


    public static void main(String[] args) {
        String dicPathFrom = "/Users/yefei.yf/IdeaWorkspace/branch/judata";
        String dicPathTo = "/Users/yefei.yf/IdeaWorkspace/judata";

        CodeDiff codeDiff = new CodeDiff(dicPathFrom, dicPathTo);
        Set<String> delCodeFileNameSet = codeDiff.getHadDelCodeFileNames();

        System.out.println("-------------" + delCodeFileNameSet.size() + "�����ļ���ɾ��----------");

        int i = 0;
        for (String delCodeName : delCodeFileNameSet) {
            if (!delCodeName.startsWith("Maven")) {
                i++;
                System.out.println(i + ". " + delCodeName);
            }

        }


        Set<String> newAddFileNameSet = codeDiff.getNewAddCodeFileNames();

        System.out.println("-------------" + newAddFileNameSet.size() + "�����ļ�����----------");

        i = 0;
        for (String newAdd : newAddFileNameSet)
            if (!newAdd.startsWith("Maven")) {
                i++;
                System.out.println(i + ". " + newAdd);
            }
    }

}