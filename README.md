# Chatcorex

A professional **chat management core plugin** for Minecraft servers.  
Supports **PlaceholderAPI**, private messaging, staff chat, mute chat, cooldowns, swear filtering, and more.  

---

## ✨ Features
- ✅ **Private Messaging** (`/msg`, `/reply`)  
- ✅ **Staff Chat** with toggle & highlight mentions (with sound notification)  
- ✅ **Mute Chat** command for moderators/admins  
- ✅ **Configurable Chat Format** with **PlaceholderAPI** support (LuckPerms prefixes, etc.)  
- ✅ **Chat Cooldown** to prevent spam  
- ✅ **Swear Filter** (customizable)  
- ✅ **Reload Command** to apply changes instantly  
- ✅ Works on **1.8.8 → latest versions** (auto sound compatibility)  

---

## 📦 Installation
1. Download the plugin `.jar`.  
2. Place it in your server’s `plugins/` folder.  
3. Restart your server.  
4. (Optional) Install [PlaceholderAPI](https://www.spigotmc.org/resources/placeholderapi.6245/) for prefixes, suffixes, etc.  

---

## ⚡ Commands
| Command | Aliases | Description | Permission |
|---------|---------|-------------|------------|
| `/msg <player> <message>` | `/tell`, `/whisper`, `/pm` | Send a private message | `chatcorex.msg` |
| `/reply <message>` | `/r` | Reply to last private message | `chatcorex.reply` |
| `/mutechat` | `/mc` | Toggle chat mute | `chatcorex.mutechat` |
| `/staffchat <message>` | `/sc`, `/staffc` | Send message to staff chat | `chatcorex.staffchat` |
| `/chatreload` | - | Reload plugin config | `chatcorex.reload` |

---

## 🔑 Permissions
| Permission | Default | Description |
|------------|---------|-------------|
| `chatcorex.msg` | true | Use private messages |
| `chatcorex.reply` | true | Reply to messages |
| `chatcorex.mutechat` | op | Mute/unmute chat |
| `chatcorex.staffchat` | op | Access staff chat |
| `chatcorex.reload` | op | Reload plugin config |

---

## ⚙️ Config Overview
```yaml
chat:
  format: "&7%rank% &b%player_name% &7» &f%message%"
  private-to: "&d✉ You → %to%: &f%message%"
  private-from: "&d✉ %from% → You: &f%message%"
  muted: "&c⚠ Chat is currently muted!"
  cooldown: "&c⚠ You must wait %seconds%s before chatting again!"
  cooldown-time: 5 # seconds


staff-chat:
  enabled: true
  permission: "chatcorex.staffchat"
  format: "&5[Staff] &b%player_name% &7» &f%message%"

anti-swear:
  enabled: true
  blocked-words:
  # here blocked words


  replacement: "***" # overridden by dynamic star-length censoring
  notify-staff: true
  staff-permission: "chatcorex.antiswear.notify"

```

👤 Author
**3bdoabk**
Made with ❤️ for Minecraft servers.
 
  permission: chatcorex.staffchat
  format: "&8[Staff] &b%player_name% &7» &f%message%"
