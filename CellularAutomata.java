import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class CellularAtomata extends PApplet {

int w = 96;
int h = 60;
int[][] initialUniverse = new int[w][h];
int[][] universe = new int[w][h];
int[][] universePRIME = new int[w][h];
int placeValue = 1;
int cx = 0;
int cy = 0;
int cxPrime = 0;
int cyPrime = 0;
int neighbors = 0;
boolean step = false;
int xpick = 0;
int ypick = 0;
int generations = 0;
int stage = 0;
String rule = "hello";
int similarity = 0;

public void setup() {
  frameRate(60);
  
  
  for (int i=0; i<w; i++) {
    for (int j=0; j<h; j++) {
      initialUniverse[i][j] = 0;
      universe[i][j] = 0;
      universePRIME[i][j] = 0;
    }
  }
}

public void renderUniverse() {
  background(0);
  stroke(10);
  strokeWeight(1);
  for (int i=0; i<width; i+=15) {
    for (int j=0; j<height; j+=15) {
      if (universe[PApplet.parseInt(i/15)][PApplet.parseInt(j/15)] == 0) {
        fill(50);
      } else {
        if (universe[PApplet.parseInt(i/15)][PApplet.parseInt(j/15)] == 1) {
          fill(255);
        } else {
          fill(50-(50/universe[PApplet.parseInt(i/15)][PApplet.parseInt(j/15)]), 0, 255/universe[PApplet.parseInt(i/15)][PApplet.parseInt(j/15)]);
        }
      }
      rect(i, j, 15, 15);
    }
  }
  textSize(31);
  fill(100, 50, 20);
  text("generations: " + str(generations), width/2, 30);
  textSize(30);
  fill(255, 220, 150);
  text("generations: " + str(generations), width/2, 30);
}

public void updateUniverse() {
  similarity = 0;
  for (int i=0; i<w; i++) {
    for (int j=0; j<h; j++) {
      if (universe[i][j] == universePRIME[i][j]) {
        similarity++;
      } else {
        universe[i][j] = universePRIME[i][j];
      }
    }
  }
  if (similarity == w*h) {
    step = false;
  }
}

public void countNeighbors() {
  neighbors = 0;
  for (int i=-1; i<2; i++) {
    for (int j=-1; j<2; j++) {
      if (i != 0 || j != 0) {
        cxPrime = (cx+i)%w;
        cyPrime = (cy+j)%h;
        if (abs(cxPrime) != cxPrime) {
          cxPrime += w;
        }
        if (abs(cyPrime) != cyPrime) {
          cyPrime += h;
        }
        if (universe[cxPrime][cyPrime] == 1) {
          neighbors++;
        }
      }
    }
    if (universe[cx][cy] > 1) {
      universePRIME[cx][cy] = 0;
    }

    if (rule == "conways") {
      if (universe[cx][cy] != 1) { //cell is dead
        if (neighbors == 3) { //birth
          universePRIME[cx][cy] = 1;
        } else {
          universePRIME[cx][cy] = 0;//dead (the same)
        }
      }
      if (universe[cx][cy] == 1) { //cell is alive
        if (neighbors < 2) { //under population
          universePRIME[cx][cy] = 2;
        } else {
          if (neighbors > 3) { //over population
            universePRIME[cx][cy] = 2;
          } else { //sustained
            universePRIME[cx][cy] = 1;
          }
        }
      }
    } else {

      if (rule == "maj") {
        if (neighbors > 4) {
          universePRIME[cx][cy] = 1;
        } else {
          if (neighbors < 4) {
            if (universe[cx][cy] == 1) {
              universePRIME[cx][cy] = 2;
            } else {
              universePRIME[cx][cy] = 0;
            }
          } else {
            if (universe[cx][cy] == 2) {
              universePRIME[cx][cy] = 0;
            } else {
              universePRIME[cx][cy] = universe[cx][cy];
            }
          }
        }
      } else {

        if (rule == "repl") {
          if (neighbors < 1) {
            universePRIME[cx][cy] = 0;
          } else {
            if (neighbors < 5) {
              universePRIME[cx][cy] = 2;
            } else {
              universePRIME[cx][cy] = 1;
            }
          }
        }
        
      }
    }
  }
}

public void stepFunc() {
  for (int i=0; i<w; i++) {
    for (int j=0; j<h; j++) {
      if (abs(i) == i && i < w) {
        if (abs(j) == j && j < h) {
          cx = i;
          cy = j;
        } else {
          cy = j%h;
          if (cy < 0) {
            cy += h;
          }
        }
      } else {
        cx = i%w;
        if (cx < 0) {
          cx += w;
        }
      }
      countNeighbors();
    }
  }
  generations++;
  updateUniverse();
  renderUniverse();
}

public void clearUniverse() {
  for (int i=0; i<w; i++) {
    for (int j=0; j<h; j++) {
      universe[i][j] = 0;
    }
  }
}

public void resetUniverse() {
  for (int i=0; i<w; i++) {
    for (int j=0; j<h; j++) {
      universe[i][j] = initialUniverse[i][j];
    }
  }
}

public void randomizeUniverse() {
  for (int i=0; i<w; i++) {
    for (int j=0; j<h; j++) {
      universe[i][j] = round(random(1));
      if (generations == 0) {
        initialUniverse[i][j] = universe[i][j];
      }
    }
  }
  renderUniverse();
}

public void keyPressed() {
  if (key == ' ') {
    step = !step;
  }
  if (key == 's') {
    stepFunc();
  }
  if (key == 'c') {
    clearUniverse();
    renderUniverse();
  }
  if (key == 'r') {
    randomizeUniverse();
  }
  if (keyCode == ENTER) {
    resetUniverse();
    generations = 0;
    renderUniverse();
  }
}

public void text() {
  text("Conway's GOL", width/6, height/2);
  text("Majority", width/2, height/2);
  text("Replication", (5*width)/6, (height/2));
}

public void draw() {
  if (stage == 0) {
    strokeWeight(3);
    textAlign(CENTER);
    fill(0, 0, 255);
    rect(0, 0, width/3, height);
    fill(0, 200, 0);
    rect(width/3, 0, width/3, height);
    fill(200, 0, 0);
    rect((2*width)/3, 0, width/3, height);
    textSize(70);
    fill(0);
    text();
    textSize(68);
    fill(255);
    text();
    if (mousePressed) {
      if (mouseX < width/3) {
        rule = "conways";
      } else {
        if (mouseX < (width*2)/3) {
          rule = "maj";
        } else {
          rule = "repl";
        }
      }
      stage = 1;
    }
  } else {
    if (stage == 1) {
      if (mouseButton == LEFT && !step) {
        xpick = floor(mouseX/15);
        ypick = floor(mouseY/15);
        if (abs(placeValue) != placeValue) {
          placeValue = 1-universe[xpick][ypick];
        }
        if (xpick < w && ypick < h) {
          universe[xpick][ypick] = placeValue; 
          if (generations == 0) {
            initialUniverse[xpick][ypick] = placeValue;
          }
        }
        renderUniverse();
      } else {
        placeValue = -1;
      }
      if (step) {
        stepFunc();
      }
    }
  }
}

  public void settings() {  size(1440, 900);  smooth(); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "--present", "--window-color=#666666", "--hide-stop", "CellularAtomata" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
