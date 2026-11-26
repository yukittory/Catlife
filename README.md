# Catlife — Forge 1.20.1 prototype

A starter project for Java 17 and Minecraft Forge 1.20.1.

Included in this prototype:
- three toy items: yarn ball, toy mouse, toy ball
- simple server-side behavior: cats wander toward nearby cats and loose toys
- small playful motion when cats meet near each other or toys

## Easiest build: GitHub Actions (no Java/Gradle install needed locally)
1. Create a GitHub account (if needed) and a new repository.
2. Upload the contents of this ZIP into the repository (the `.github` folder must be included).
3. Open the **Actions** tab. If asked, enable workflows.
4. The build workflow runs on upload to `main`; alternatively select **Build Catlife mod** and click **Run workflow**.
5. When the run finishes successfully, open it and download the artifact named **Catlife-mod-jar**.
6. Put the resulting `.jar` in your Minecraft instance's `mods` folder. Install Forge 1.20.1 first.

This is an early prototype; custom cat animations, beds, scratching posts, and full play-state AI are not yet implemented.
