# VoA-Helper

Voa Helper is an open source helper app for Samsung Galaxy A52s that lets the user install and manage windows on his phone

<img src="https://github.com/VendDair/VoA-Helper/blob/main/Guide%20stuff/app_screenshot.jpg" alt="Image" width="300" />

*The image corresponds to the main branch and not the beta/release builds.*

### How to use the install feature
Requirements:
- Unlocked bootloader
- Root
- wim/esd file of Windows
- Free space. Not less then 15GB
- Backups because formatting data will be required
- [All partitions set](https://github.com/VendDair/VoA-Helper/blob/main/Guide%20stuff/PARTITIONS.md)

The first step is to decide what Windows version you want.
There are two options:
- Pre-made images. They are faster and preconfigured with all necessary things (drivers).
- Generic images. They are a bit slower to install, as you'll need to configure them yourself.

So where do you get a pre-made image you may ask.
It's simple. They can be found here:
- [Windows 11 Pro 22H2 with Atlas OS](https://www.dropbox.com/scl/fi/cc9e3btnzs34bmnlbvpqe/win11_22h2_atlasos_desktop.wim?rlkey=35iuwtqzw4ofrut8d3z2m17w4&e=1&st=e7it86jw&dl=0)

For generic Windows images, you can use [WoR Poject](https://worproject.com/esd)

Now you need to open the [app](https://github.com/VendDair/VoA-Helper/releases)

Open *Download Manager* and install all **winpe**, **drivers** and **uefi**

After that go back and press *Install Windows* and press *yes*

Then select your preferred method, select the wim/esd listed there (If you put a number that is not listed, the app will crash) and select the index listed there you want

So that's all. Now go make a cup of tea because this will take a few minutes *(not more then 5)*. When you're back, Windows should already be installed :)

*If there are any issues please mention them [here](https://t.me/a52sxq_uefi) with screenshots/pics and any other details that can help fixing the problem*

## FAQ
- How do I boot back int Android? Flash boot.img from /sdcard/ in TWRP or launch the "Android" app on the Windows desktop
- How do I uninstall Windows? [here](https://github.com/VendDair/VoA-Helper/blob/main/Guide%20stuff/UNINSTALL.md)


















