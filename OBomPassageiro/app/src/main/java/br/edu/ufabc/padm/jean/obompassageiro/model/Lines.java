package br.edu.ufabc.padm.jean.obompassageiro.model;

import android.content.res.Resources;
import android.location.Location;

import java.util.ArrayList;

import br.edu.ufabc.padm.jean.obompassageiro.R;

public class Lines {

    private ArrayList<Line> lines;

    public Lines() {
        this.lines =  new ArrayList<Lines.Line>();
        initExamples();
    }

    public ArrayList<String> getNamesList() {
        ArrayList<String> namesList = new ArrayList<>();

        namesList.add("Selecione o destino");

        for (Line l : lines ) {
            namesList.add(l.getName());
        }
        return namesList;
    }

    private void initExamples() {
        lines.add(new Line(1, "CPTM - Brás", null));
        lines.add(new Line(1, "CPTM - Rio Grande da Serra", null));
        lines.add(new Line(1, "Fretado - Campus Santo André", null));
        lines.add(new Line(1, "Fretado - Campus São Bernardo", null));
        lines.add(new Line(1, "Fretado - Terminal Leste", null));
        lines.add(new Line(1, "Fretado - Terminal São Bernardo", null));
    }

    public class Line {

        private int id;
        private String name;
        private Location location;
        private boolean checked;

        public Line() {
        }

        public Line(int id, String name, Location location) {
            this.id = id;
            this.name = name;
            this.location = location;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Location getLocation() {
            return location;
        }

        public void setLocation(Location location) {
            this.location = location;
        }

        public boolean isChecked() {
            return checked;
        }

        public void setChecked(boolean checked) {
            this.checked = checked;
        }

        @Override
        public String toString() {
            return this.name;
        }
    };
}
