# AGENTS.md — EnchantShield Mod

Este archivo contiene el contexto persistente del proyecto para agentes/colaboradores.
Léelo antes de tocar nada.

## Objetivo del mod

Mod de Fabric para **Minecraft 26.3 (Java 25)** que añade **4 escudos personalizados**
que **se pueden encantar**:

| Escudo | id (`enchantshield-mod:`) | Durabilidad | Encantabilidad | Repara con |
| ------ | ------------------------- | ----------- | -------------- | ---------- |
| Encantable | `enchantable_shield` | 336 | 15 | `minecraft:iron_ingot` |
| Reforzado | `reinforced_shield` | 672 | 10 | `minecraft:netherite_ingot` |
| Místico | `mystical_shield` | 252 | 25 | `minecraft:amethyst_shard` |
| Madera | `wooden_shield` | 112 | 15 | `#minecraft:planks` |

Además, el mod define un **encantamiento propio** (`enchantshield-mod:example_shield_enchantment`,
"Projectile Protection") que reduce daño de proyectiles cuando el escudo está equipado
en la mano secundaria, y usa un **renderizador de modelo especial** para mostrar la
textura 3D propia de cada escudo.

## Entorno y build

- Requiere un **JDK 25** disponible (en el `PATH` o donde el wrapper lo encuentre).
- **Compilar (verificación principal):**

  ```powershell
  .\gradlew build --no-daemon
  ```

  Genera `build\libs\enchantshield-mod-1.0.0.jar`.
- **Ejecutar el juego en desarrollo:**

  ```powershell
  .\gradlew runClient
  ```

  (en Windows/PowerShell usa `.\gradlew`; en bash `./gradlew`).
- El wrapper está fijado a **Gradle 9.7.1** (`gradle/wrapper/gradle-wrapper.properties`).
  Loom 1.18-SNAPSHOT resuelve a **1.18.3**, que exige Gradle 9.7+.

### Versiones (gradle.properties)

```
minecraft_version=26.3
loader_version=0.19.5
loom_version=1.18-SNAPSHOT   -> resuelve 1.18.3
fabric_api_version=0.161.0+26.3
version=1.0.0
group=shield.modid
```

- `build.gradle` usa `splitEnvironmentSourceSets()` → hay **dos sourceSets**:
  `main` y `client`. Java release/toolchain = **25**.
- Dependencia clave: `fabric-api` aplica los **transitive access wideners** que hacen
  accesibles miembros como `SpecialModelRenderers.ID_MAPPER` o `RenderTypes.entitySolidGlint`.

## Estado actual (hecho)

- [x] 4 escudos registrados con `ShieldTemplate`/`ModShields`; se añaden a la pestaña
      creativa **COMBAT** vía `ModCreativeTab` (`CreativeModeTabEvents.modifyOutputEvent`).
- [x] Propiedades de escudo equivalentes al vanilla (`BlocksAttacks` 90°, sonidos,
      `equippableUnswappable(OFFHAND)`, `BANNER_PATTERNS=EMPTY`, `BREAK_SOUND`) + `.enchantable(n)`.
- [x] Recetas (`data/enchantshield-mod/recipe/*.json`), tags de reparación y tag
      `enchantshield-mod:shields`.
- [x] Escudos añadidos a `#minecraft:enchantable/durability` (habilita Unbreaking/Mending).
- [x] Encantamiento custom en `data/enchantshield-mod/enchantment/example_shield_enchantment.json`
      y añadido a `#minecraft:in_enchanting_table` para que salga en la mesa.
- [x] **Espinas (Thorns) aplicable a los escudos**: se sobrescribe
      `data/minecraft/enchantment/thorns.json` cambiando `supported_items`/`primary_items` por los
      tags del mod `enchantshield-mod:thorns_supported` / `enchantshield-mod:thorns_primary`
      (que unen `#minecraft:enchantable/armor`+`#minecraft:enchantable/chest_armor` con
      `#enchantshield-mod:shields`). Thorns tiene `slots: ["any"]`, así que **funciona** en la
      mano secundaria. Solución dirigida: solo Thorns cambia, NO se activan Protection / Fire /
      Blast / Projectile Protection en escudos. Verificado en runtime (mesa de encantamientos y
      libro en el yunque).
- [x] **Espinas hace daño al atacar un escudo encantado**: mixin
      `ShieldThornsBlockingMixin` (common, `enchantshield-mod.mixins.json`) que, en
      `LivingEntity#applyItemBlocking`, cuando el golpe queda **totalmente bloqueado**
      (daño restante 0), invoca la pipeline vanilla
      `EnchantmentHelper.doPostAttackEffects` (la misma de `post_attack`). Sin el mixin,
      `hurtServer` devuelve `false` con bloqueo completo y el atacante nunca procesa los
      efectos `post_attack` de la víctima → Thorns nunca disparaba. Con el mixin, Espinas
      aplica su daño (1-5) y desgasta el escudo (+2) con su probabilidad de 50 %/nivel
      (ajustada en `thorns.json`; el vanilla es 15 %/nivel),
      usando el JSON data-driven de `thorns.json`. Solo dispara en bloqueo total para no
      duplicar el disparo cuando queda daño restante (ahí vanilla ya lo procesa).
- [x] **Respiración acuática (Respiration) aplicable a los escudos, activa solo al bloquear**:
      se sobrescribe `data/minecraft/enchantment/respiration.json` cambiando `supported_items`
      por el tag `enchantshield-mod:respiration_supported` (une
      `#minecraft:enchantable/head_armor` con `#enchantshield-mod:shields`), de modo que la mesa
      y el yunque aceptan Respiración en los escudos. En 26.3 Respiración es data-driven y aplica
      el atributo `minecraft:oxygen_bonus` **solo cuando el objeto está en una de sus `slots`**
      (`head`), y un escudo bloqueado no ocupa ranura, así que el efecto no se aplicaría. El mixin
      `ShieldRespirationBlockingMixin` (common) reproduce la misma fórmula de
      `LivingEntity#decreaseAirSupply` (prob. de no consumir aire = bonus/(bonus+1), bonus = 1 por
      nivel) leyendo el nivel del escudo con el que se está bloqueando
      (`getItemBlockingWith()`). Si el escudo no tiene Respiración, no hace nada.
- [x] Renderizador custom `CustomShieldSpecialRenderer` + registro en `onInitializeClient`.
- [x] Modelos de item (normal/bloqueo) y definiciones de item usan el tipo
      `enchantshield-mod:custom_shield` con su textura.
- [x] Limpieza: eliminados mixins de ejemplo, `EnchantmentTemplate`, tags/claves/lang sin uso,
      `shield_border.png`. `fabric.mod.json` ya no declara mixins.
- [x] `build` pasa (BUILD SUCCESSFUL) y los 40+ JSON son válidos.

**Verificado en runtime (servidor de desarrollo):** la mesa de encantamientos ofrece el
encantamiento custom, Unbreaking y **Espinas (Thorns)** en los escudos. Pendiente en cliente
(`runClient`): confirmar que (b) los escudos se ven con su textura propia al sostenerlos y
(c) bloquean daño y se reparan en el yunque.

## Estructura del proyecto

```
build.gradle                          # Loom + splitEnvironmentSourceSets
gradle.properties                     # versiones
gradle/wrapper/gradle-wrapper.properties  # Gradle 9.7.1
gradlew, gradlew.bat, gradlew.ps1     # wrappers
tools/                                # scripts dev que generan texturas (no se compilan)
  GenerateTextures.java                # OBSOLETO: dibuja en todo el canvas (layout UV roto)
  GenerateShieldTexturesV2.java        # layout UV vanilla correcto (usar este); voltea el arte verticalmente
  generate_shield_textures.py
src/main/java/shield/modid/
  EnchantShieldMod.java               # ModInitializer (MOD_ID="enchantshield-mod", id(String))
  item/ShieldTemplate.java            # propiedades del escudo (enchantable + BlocksAttacks + repairable)
  item/ModShields.java                # registro de los 4 escudos
  item/ModCreativeTab.java            # añade a COMBAT
  mixin/ShieldThornsBlockingMixin.java  # Thorns se dispara al bloquear con escudo encantado
  mixin/ShieldRespirationBlockingMixin.java # Respiración activa al bloquear (oxygen_bonus)
src/client/java/shield/modid/client/
  EnchantShieldModClient.java         # ClientModInitializer -> registra el special model type
  render/CustomShieldSpecialRenderer.java  # renderer + Unbaked(MapCodec)
src/main/resources/
  fabric.mod.json
  assets/enchantshield-mod/
    items/*.json                      # item definitions (condition using_item -> special custom_shield)
    models/item/*.json (+_blocking)   # solo display + particle (la geometría la pone el renderer)
    textures/item/*.png               # textura 2D / particle
    textures/entity/shield/*_base.png # textura 3D usada por el renderer (atlas shield_patterns)
    lang/en_us.json, lang/es_es.json
  data/enchantshield-mod/
    recipe/*.json
    tags/item/shields.json, *_repair.json
    tags/item/thorns_supported.json, thorns_primary.json  # armor+escudos para Thorns
    tags/item/respiration_supported.json  # head_armor+escudos para Respiración
    enchantment/example_shield_enchantment.json
  data/minecraft/
    enchantment/thorns.json           # override: Thorns también acepta escudos
    enchantment/respiration.json       # override: Respiración también acepta escudos
    tags/
      item/enchantable/durability.json  # añade los 4 escudos (Unbreaking/Mending)
      enchantment/in_enchanting_table.json  # añade el encantamiento custom a la mesa (replace:false)
```

> **Ojo con las carpetas de data en 26.3:** son **singulares** (`recipe/`, `tags/item/`,
> `enchantment/`, `loot_table/`, `advancement/`). En `assets/` sí es `items/` (plural).
> Ponerlas en plural hace que el datapack NO cargue.

## Cómo funciona (arquitectura)

### Items (Java)
`ShieldTemplate.createShieldProperties(maxDamage, id, enchantability, repairTag)` construye
`Item.Properties` con `.durability().stacksTo(1).setId().enchantable(n)`, los componentes
vanilla de escudo y `.repairable(repairTag)`. `ModShields.registerShield(...)` crea un
`ShieldTemplate extends ShieldItem` y lo registra en `BuiltInRegistries.ITEM`.

### Datapack
- **Recetas** `crafting_shaped` (formato 26.3: el result usa `"id"`, los ingredientes son
  strings o `"#tag"`; `{"item": ...}` es inválido).
- **Tags de reparación** apuntan a un item o tag vanilla.
- **Encantamiento**: JSON declarativo en `data/<ns>/enchantment/`.

### Renderizado (cliente)
- La definición de item (`assets/enchantshield-mod/items/escudo.json`) usa
  `minecraft:condition` (`using_item`) → `minecraft:special` con
  `"model": {"type":"enchantshield-mod:custom_shield","texture":"enchantshield-mod:<nombre>_base"}`.
- `CustomShieldSpecialRenderer.Unbaked.bake(...)` construye un `SpriteId` con
  `Sheets.SHIELD_MAPPER.apply(texture)` → sprite `enchantshield-mod:entity/shield/<nombre>_base`
  del atlas `textures/atlas/shield_patterns.png` (su source es `entity/shield`, incluye moddeds).
- El tipo se registra en `SpecialModelRenderers.ID_MAPPER` dentro de `onInitializeClient`
  (`Identifier.fromNamespaceAndPath(MOD_ID, "custom_shield")`).
- Se reutiliza la lógica vanilla de `ShieldSpecialRenderer` (foil + patrones de estandarte).

## Mecánicas de Minecraft 26.3 verificadas (referencia)

Fuentes decompiladas de referencia (pueden borrarse; ver sección "Re-obtener fuentes"):
- Código common: `<temp>\opencode\mcsrc`
- Código client: `<temp>\opencode\mcclientsrc`
- Data/assets vanilla: `<temp>\opencode\mcdata`

### Requisitos para encantar en la mesa
1. `ItemStack.isEnchantable()` = tener componente `ENCHANTABLE` **y** no tener encantamientos.
   → `.enchantable(n)` en las Properties. (`ItemStack.java:999`)
2. `EnchantmentMenu.getEnchantmentList` toma como **fuente únicamente** el tag
   `EnchantmentTags.IN_ENCHANTING_TABLE` = `minecraft:in_enchanting_table`. El encantamiento
   debe estar ahí. (`EnchantmentMenu.java:187`)
3. `EnchantmentHelper.getAvailableEnchantmentResults` filtra por
   `enchantment.isPrimaryItem(stack)` = estar en `supported_items` y (si existe
   `primary_items`, estar también ahí). (`EnchantmentHelper.java:587`)
4. El coste (`getEnchantmentCost`) debe caer en `[min_cost(level), max_cost(level)]`.
5. La **encantabilidad** del item entra en `selectEnchantment`:
   `cost += 1 + rnd(n/4+1) + rnd(n/4+1)` y ±15%. Más encantabilidad ⇒ mejores niveles.
   (`EnchantmentHelper.java:540-571`)

### Tags vanilla relevantes (data/minecraft/tags)
- `item/enchantable/durability.json` incluye `minecraft:shield`. **Para que Unbreaking y
  Mending apliquen a tus escudos, añádelos a este tag** (ya hecho).
- `item/enchantable/vanishing.json` = `#enchantable/durability` + compass/pumpkin/skulls; por
  herencia tus escudos ya entran (Curse of Vanishing).
- `enchantment/in_enchanting_table.json` = `#minecraft:non_treasure`. **Añade el tuyo con
  `"replace": false`** para que se fusione (no borre los vanilla).
- `enchantment/non_treasure.json` (ofrecibles en mesa) vs `treasure.json` (botín: mending,
  vanishing_curse, swift_sneak, soul_speed, frost_walker, wind_burst, binding_curse).
  Unbreaking SÍ está en non_treasure; **Mending NO** (solo yunque/loot/trade).
- **No existe** `minecraft:shields` como tag de item vanilla (no lo crees).

### Esquema JSON del encantamiento (`Enchantment.DIRECT_CODEC`)
```jsonc
{
  "description": { "translate": "enchantment.<ns>.<path>" },   // requerido
  "supported_items": "#<ns>:<tag>" | [ "item", ... ],           // requerido
  "primary_items": "...",                                       // opcional
  // OJO: en formato lista SOLO van IDs de item directos (sin "#").
  // Para combinar tags (vanilla + mod) usa un tag propio que las una y pásalo
  // como string: "supported_items": "#<ns>:<tag_que_une>".
  "weight": 1..1024,                                            // requerido
  "max_level": 1..255,                                          // requerido
  "min_cost": { "base": int, "per_level_above_first": int },    // requerido
  "max_cost": { "base": int, "per_level_above_first": int },    // requerido
  "anvil_cost": int >= 0,                                       // requerido
  "slots": [ "any"|"mainhand"|"offhand"|"hand"|"armor"|... ],   // requerido
  "exclusive_set": "...",                                       // opcional
  "effects": { ... }                                            // opcional
}
```
Los campos de `EnchantmentDefinition` van **planos** en la raíz (no anidados en "definition").
`slots` usa `EquipmentSlotGroup` (constantes: `any`, `mainhand`, `offhand`, `hand`, `armor`,
`head`, `chest`, `legs`, `feet`, `body`).

### `EnchantmentEffectComponents` disponibles (efectos válidos)
`damage_protection`, `damage_immunity`, `damage`, `smash_damage_per_fallen_block`,
`knockback`, `armor_effectiveness`, `post_attack`, `post_piercing_attack`, `hit_block`,
`item_damage`, `equipment_drops`, `location_changed`, `tick`, `ammo_use`,
`projectile_piercing`, `projectile_spawned`, `projectile_spread`, `projectile_count`,
`trident_return_acceleration`, `fishing_time_reduction`, `fishing_luck_bonus`,
`block_experience`, `mob_experience`, `repair_with_xp`, `attributes`,
`crossbow_charge_time`, `crossbow_charging_sounds`, `trident_sound`,
`prevent_equipment_drop`, `prevent_armor_change`, `trident_spin_attack_strength`.
Tipos de efecto valor: `add`/`multiply`/`remove_binomial`/`all_of`/`set`... (ver
`world/item/enchantment/effects/`). Condiciones: `damage_source_properties`, `match_tool`,
`inverted`, etc. (ver `world/item/enchantment/ConditionalEffect` y loot predicates).

### Escudo vanilla (`Items.SHIELD`, `ShieldItem`)
```
.durability(336)
.component(BANNER_PATTERNS, BannerPatternLayers.EMPTY)
.repairable(ItemTags.WOODEN_TOOL_MATERIALS)
.equippableUnswappable(EquipmentSlot.OFFHAND)
.delayedComponent(BLOCKS_ATTACKS, ctx -> new BlocksAttacks(
    0.25F, 1.0F,
    List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 1.0F)),
    new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
    Optional.of(ctx.getOrThrow(DamageTypeTags.BYPASSES_SHIELD)),
    Optional.of(SoundEvents.SHIELD_BLOCK),
    Optional.of(SoundEvents.SHIELD_BREAK)))
.component(BREAK_SOUND, SoundEvents.SHIELD_BREAK)
```
`ShieldItem.getName` colorea por `DataComponents.BASE_COLOR`.

### Render especial (client)
- Los tipos de modelo especial se registran en
  `net.minecraft.client.renderer.special.SpecialModelRenderers.ID_MAPPER`
  (`LateBoundIdMapper<Identifier, MapCodec<? extends SpecialModelRenderer.Unbaked<?>>>`),
  poblado por `SpecialModelRenderers.bootstrap()` llamado en `ClientBootstrap.bootstrap()`.
  **No hay hook de Fabric API** → se registra a mano con `ID_MAPPER.put(...)` en
  `onInitializeClient`. El `codec()` mantiene la referencia al mapa, así que registrar antes
  de cargar recursos es correcto.
- `Sheets.SHIELD_MAPPER = new SpriteMapper(textures/atlas/shield_patterns.png, "entity/shield")`
  → `apply(id)` usa la textura `<id.namespace>:entity/shield/<id.path>`.
- El atlas `assets/minecraft/atlases/shield_patterns.json` tiene source `directory`
  `entity/shield` → recoge las texturas de **todos** los namespaces.
- Firma útil: `submitNodeCollector.submitModel(model, state, poseStack, light, overlay, -1 /*tint*/,
  spriteId, sprites, outline)` y la variante con `RenderType`+sprite para foil.
- `RenderTypes.entitySolidGlint(atlas)` y `RenderTypes.patternedShieldGlint()`.
- `BannerRenderer.submitPatterns(sprites, poseStack, collector, light, overlay, model, state,
  false, dyeColor, patterns)` para patrones.

## Trampas conocidas (pitfalls)

1. **No hay un JDK 25 disponible** → el wrapper falla con "JAVA_HOME is not set and no 'java'
   command could be found". Usa un JDK 25 donde el wrapper lo encuentre (PATH o toolchain).
2. **Carpetas de data en singular**. Si el datapack "no hace nada", revisa que sea
   `data/<ns>/recipe/`, `tags/item/`, `enchantment/`.
3. **`in_enchanting_table` con `replace: true`** borraría todos los encantamientos vanilla de
   la mesa. Usa `replace: false`.
4. **El encantamiento no sale en la mesa** si no está en `in_enchanting_table`, o si los
   escudos no están en su `supported_items` (`#enchantshield-mod:shields`), o si el escudo no
   tiene `ENCHANTABLE`.
5. **Unbreaking/Mending no aplican** si los escudos no están en `#minecraft:enchantable/durability`.
6. **Textura 3D no aparece** si: el `type` del modelo no está registrado antes de cargar
   recursos, el `texture` no coincide con `entity/shield/<...>` en el atlas, o el item
   definition no usa `enchantshield-mod:custom_shield`.
7. **Regenerar texturas con `tools/`** puede recrear archivos; el `.py` tiene una ruta
   hardcodeada a otro equipo (`C:\Users\videojuegos\...`). Ajusta `output_dir` o usa el Java.
   **OJO con el layout UV del escudo**: el `ShieldModel` vanilla (placa `texOffs(0,0)`,
   box 12x22x1 sobre canvas 64x64) solo "ve" las regiones norte `(1,1)-(13,23)` y sur
   `(14,1)-(26,23)`, más el mango `(26,0)-(42,12)`. Si dibujas el arte en TODO el canvas,
   el inventario muestra solo el cuadrante superior-izquierdo (escudo pequeño/cortado).
   Usa `GenerateShieldTexturesV2.java` (layout correcto); `GenerateTextures.java` y el `.py`
   antiguos dibujan en todo el canvas y están obsoletos.
8. **Mixins**: hay dos common (`ShieldThornsBlockingMixin` y `ShieldRespirationBlockingMixin`,
   `enchantshield-mod.mixins.json`, declarado en `fabric.mod.json`). Si añades mixins de
   cliente, crea `enchantshield-mod.client.mixins.json` y añádelo a `fabric.mod.json` con
   `"environment": "client"`. El config usa `compatibilityLevel: "JAVA_25"` y
   `injectors.defaultRequire: 1` (si el target no aplica, el juego no arranca).
9. **Namespaces con guion** (`enchantshield-mod`) son válidos; el path NO debe llevar mayúsculas.
10. **`supported_items`/`primary_items` en formato lista NO aceptan tags** (`"#minecraft:..."`).
    El formato lista es solo IDs de item directos; un `#tag` da
    `Not a valid resource location: #...`. Para unir tags usa **un tag propio** que las
    referencie y pásalo como string único (así se hace en `enchantshield-mod:thorns_supported`).
11. **Sobrescribir un encantamiento vanilla** (p.ej. `data/minecraft/enchantment/thorns.json`)
    SÍ funciona desde un mod (el pack del mod se aplica después de vanilla), pero reemplaza la
    definición **entera**: copia TODOS los campos vanilla y cambia solo lo necesario. Si falta
    un campo obligatorio, el arranque falla con
    `Failed to parse minecraft:<encantamiento> from pack <mod>`.

## Próximos pasos / ideas

- **Probar en juego** (`runClient`) y ajustar costes/encantabilidad si el encantamiento sale
  demasiado o muy poco.
- Opcional: hacer el encantamiento aplicable a **libros** (ya funciona: `isBook` en
  `getAvailableEnchantmentResults`) y permitir comerciar con él añadiéndolo a
  `#minecraft:tradeable`.
- Opcional: más encantamientos por escudo (p.ej. `damage_immunity`, `knockback`,
  `attributes` con armadura/velocidad).
- Opcional: `on_shelf` en los modelos (26.3 tiene estanterías); el modelo vanilla de escudo lo
  incluye y los nuestros no.
- Opcional: tooltips propios (override `appendHoverText` en `ShieldTemplate` o componente
  `TooltipProvider`).
- Opcional: texturas `_base_nopattern` por escudo y usar dos `SpriteId` en el renderer para
  replicar exactamente el vanilla (hoy usa la misma base con y sin patrones).
- Opcional: `icon.png`/descripción de `fabric.mod.json` (aún dice "example description").

## Re-obtener fuentes/data de Minecraft (si se borra el temp)

Los jars deobfuscados de Loom están en:
```
~/.gradle/caches/fabric-loom/minecraftMaven/net/minecraft/
  minecraft-common-deobf/26.3/minecraft-common-deobf-26.3.jar
  minecraft-clientonly-deobf/26.3/minecraft-clientonly-deobf-26.3.jar
```
- Para código con nombres legibles: `.\gradlew genSources` (Loom genera sources).
- Para extraer assets/atlases/lang rápidamente, abre el jar con `System.IO.Compression.ZipFile`
  (PowerShell) o `jar tf`.
- Verificación de JSON del proyecto:
  ```powershell
  Get-ChildItem -Recurse -File -Path src -Filter *.json |
    ForEach-Object { Get-Content -Raw $_.FullName | ConvertFrom-Json | Out-Null }
  ```

## Convenciones de código

- Java 25, sin comentarios salvo cuando aportan (muchos archivos ya existentes los tienen en
  español; mantén el idioma del archivo que edites).
- Nada de escribir en `log`/consola para "comunicar" cambios.
- No commitear salvo petición explícita; no tocar `git config`.
