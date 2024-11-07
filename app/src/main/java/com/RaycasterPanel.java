package com;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class RaycasterPanel extends JPanel implements Runnable {
    private KeyHandler keyHandler = new KeyHandler(this);
    Thread raycasterThread;
    int targetFPS = 60;

    int playerWidth = 5;
    int playerHeight = 5;
    double playerX, playerY, playerAngle, playerDeltaX, playerDeltaY;

    int mapX = 8;
    int mapY = 8;

    double PI = 3.14159f;

    int mapScale = 64;
    int[] map = {
            1, 1, 1, 1, 1, 1, 1, 1,
            1, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 1, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 1,
            1, 0, 0, 0, 0, 2, 0, 1,
            1, 0, 0, 0, 0, 0, 0, 1,
            1, 1, 1, 1, 1, 1, 1, 1,
    };

    public RaycasterPanel() {
        this.setPreferredSize(new Dimension(1024, 512));
        this.setBackground(Color.GRAY);
        this.addKeyListener(keyHandler);
        this.setFocusable(true);

        playerX = 300;
        playerY = 300;
        playerAngle = 0;

        this.startThread();
    }

    private void drawRays(Graphics g) {

        int amountOfRays = 100;
        int fowAsDegrees = 60;
        double fow = fowAsDegrees * (PI / 180);
        double halfOfFow = fowAsDegrees / 2 * (PI / 180);

        int startX = 530;
        int endX = getWidth();
        int totalWidth = endX - startX;

        for (int i = 0; i < amountOfRays; i++) {

            int lineStartX = (int) Math.floor(playerX + playerWidth / 2);
            int lineStartY = (int) Math.floor(playerY + playerHeight / 2);

            double rayAngle = playerAngle - halfOfFow + i * (fow) / amountOfRays;

            double rayDeltaX = Math.cos(rayAngle);
            double rayDeltaY = Math.sin(rayAngle);

            int directionArrowLength = (int) getRayDistance(g, rayDeltaX, rayDeltaY);
            int lineEndX = (int) Math.floor(lineStartX + rayDeltaX * directionArrowLength);
            int lineEndY = (int) Math.floor(lineStartY + rayDeltaY * directionArrowLength);

            g.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);

            double ca = playerAngle - rayAngle;
            if (ca < 0) {
                ca += 2 * PI;
            }
            if (ca > 2 * PI) {
                ca -= 2 * PI;
            }

            directionArrowLength = (int) Math.floor(directionArrowLength * Math.cos(ca));
            double lineH = (mapScale * 500 / directionArrowLength);

            if (lineH > 500) {
                lineH = 500;
            }

            int lineTop = (getHeight() / 2) - (int) (lineH / 2);

            int rayX = startX + i * (totalWidth / amountOfRays);

            g.fillRect(rayX, lineTop, totalWidth / amountOfRays, (int) lineH);
        }
    }

    private double getRayDistance(Graphics g, double rayDeltaX, double rayDeltaY) {
        double distance = 0.0;

        double x = playerX + playerWidth / 2;
        double y = playerY + playerHeight / 2;

        boolean hitWall = false;

        while (!hitWall) {
            distance++;

            x += rayDeltaX;
            y += rayDeltaY;

            int mapX = (int) Math.floor(x / mapScale);
            int mapY = (int) Math.floor(y / mapScale);

            if (mapX < 0 || mapX >= this.mapX || mapY < 0 || mapY >= this.mapY
                    || this.map[mapY * this.mapX + mapX] == 1) {
                hitWall = true;
                g.setColor(Color.RED);
            }

            if (mapX < 0 || mapX >= this.mapX || mapY < 0 || mapY >= this.mapY
                    || this.map[mapY * this.mapX + mapX] == 2) {
                hitWall = true;
                g.setColor(Color.BLUE);
            }
        }

        return distance;
    }

    private void drawMap2D(Graphics g) {

        for (int i = 0; i < Math.sqrt(map.length); i++) {
            for (int j = 0; j < Math.sqrt(map.length); j++) {

                if (map[i * (int) Math.sqrt(map.length) + j] == 0) {
                    g.setColor(Color.BLACK);
                } else if (map[i * (int) Math.sqrt(map.length) + j] == 1) {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(j * mapScale, i * mapScale, mapScale - 1, mapScale - 1);

            }

        }

    }

    private void drawDirection(Graphics g) {

        int directionArrowLength = 20; // PX

        int lineStartX = (int) Math.floor(playerX + playerWidth / 2);
        int lineStartY = (int) Math.floor(playerY + playerHeight / 2);

        int lineEndX = (int) Math.floor(lineStartX + playerDeltaX * directionArrowLength);
        int lineEndY = (int) Math.floor(lineStartY + playerDeltaY * directionArrowLength);

        g.setColor(Color.YELLOW);
        g.drawLine(lineStartX, lineStartY, lineEndX, lineEndY);

    }

    private void update() {
        if (keyHandler.leftPressed) {
            playerAngle -= 1 * (PI / 180) * 2;
            if (playerAngle < 0) {
                playerAngle = 2 * PI;
            }
        }
        if (keyHandler.rightPressed) {
            playerAngle += 1 * (PI / 180) * 2;
            if (playerAngle > 2 * PI) {
                playerAngle = 0;
            }
        }

        playerDeltaX = Math.cos(playerAngle);
        playerDeltaY = Math.sin(playerAngle);

        int xo = 0;
        if (playerDeltaX < 0) {
            xo -= 20;
        } else {
            xo = 20;
        }

        int yo = 0;
        if (playerDeltaY < 0) {
            yo -= 20;
        } else {
            yo = 20;
        }
        int ipx = (int) playerX / 64;
        int ipxAddXo = (int) (playerX + xo) / 64;
        int ipxSubXo = (int) (playerX - xo) / 64;
        int ipy = (int) playerY / 64;
        int ipyAddYo = (int) (playerY + yo) / 64;
        int ipySubYo = (int) (playerY - yo) / 64;

        if (keyHandler.upPressed) {

            if (map[ipy * mapX + ipxAddXo] == 0) {
                playerX += playerDeltaX * 0.2 * 7;
            }

            if (map[ipyAddYo * mapX + ipx] == 0) {
                playerY += playerDeltaY * 0.2 * 7;
            }
        }
        if (keyHandler.downPressed) {

            if (map[ipy * mapX + ipxSubXo] == 0) {
                playerX -= playerDeltaX * 0.2 * 7;
            }

            if (map[ipySubYo * mapX + ipx] == 0) {
                playerY -= playerDeltaY * 0.2 * 7;
            }
        }

    }

    private void startThread() {
        raycasterThread = new Thread(this);
        raycasterThread.start();
    }

    @Override
    public void run() {

        double drawInterval = 1000000000 / targetFPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        long timer = 0;

        while (raycasterThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime) / drawInterval;
            timer += (currentTime - lastTime);
            lastTime = currentTime;

            if (delta >= 1) {
                update();
                repaint();
                delta--;
            }

            if (timer >= 1000000000) {

                timer = 0;

            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        drawMap2D(g);

        // Set the color for the pixel
        g.setColor(Color.RED);

        // Draw the player as a 5x5 pixel
        g.fillRect((int) playerX, (int) playerY, playerWidth, playerHeight);
        drawDirection(g);
        drawRays(g);

    }

}
