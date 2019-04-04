package com.eunan.tracey.etimefinalyearproject.project;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RecyclerViewAdapterTest {


    Context context;
    List<ProjectModel> projectModelList = new ArrayList<>();
    ProjectModel projectModel = new ProjectModel();
    ProjectModel projectModel2 = new ProjectModel();

    @Before
    public void setUp(){
        projectModel.setProjectLocation("Coleraine");
        projectModel.setProjectName("Coleraine");
        projectModel.setProjectTimestamp( (int) System.currentTimeMillis());
        projectModel2.setProjectLocation("");
        projectModel2.setProjectName("Maghera");
        projectModel2.setProjectTimestamp( (int) System.currentTimeMillis());

        projectModelList.add(projectModel);
        projectModelList.add(projectModel2);
    }

    @Test
    public void deleteProject() {

        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(context,projectModelList);
        assertTrue(recyclerViewAdapter.deleteProject(projectModelList.get(0)));
        assertFalse(recyclerViewAdapter.deleteProject(projectModelList.get(1)));
    }
}