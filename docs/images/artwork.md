# GUI artwork provenance

The interface uses an original dark navy, cyan, and gold composition inspired by Stark/FRIDAY technology.
No layout or code was adopted from the linked course showcase projects.

- `src/main/resources/images/user-hero.png`: the Iron Spider image supplied by the user as
  `Screenshot 2026-09-13 at 00.56.39.png`. Iron Spider/Spider-Man is a Marvel character.
  The original illustrator and publication source were not provided. The image is preserved as supplied;
  JavaFX displays a rounded, square viewport around the mask and upper torso. Its original background remains.
- `src/main/resources/images/friday-core.png`: generated with the built-in OpenAI image-generation tool
  on 2026-09-13 for this project. The PNG has transparency outside the core.
- The original `avatars.png` is retained as historical artwork but is no longer loaded by the GUI.
- GUI screenshots in this directory are actual JavaFX renders from the desktop acceptance tests,
  using temporary sample data. They exclude operating-system window borders.

The generated Tony Stark portrait was superseded by the user's supplied Iron Spider artwork and is not
included in the application. The requested generated Iron Spider portrait was rejected by the image tool;
no rejected generated asset is used.

## Final FRIDAY core generation prompt

> Use case: stylized-concept. Single square avatar PNG with genuinely transparent background for FRIDAY AI assistant.
> A luminous cyan circular AI core with concentric dark navy metallic rings, restrained gold segments and bright
> central cyan light. Premium clean stylized illustration, simple bold silhouette readable at 36 pixels, front-on
> centered fills 90% square. No face, no humanoid, no text, no frame, no backdrop. Transparent outside the circular core.
