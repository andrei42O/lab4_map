package com.company;

import com.company.tests.Tests;
import com.company.ui.UI;

public class Main {
    public static void main(String[] args) throws Exception {
        Tests.run();
        UI app = new UI();
        app.run();
    }
}
