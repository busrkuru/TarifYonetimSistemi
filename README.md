# Tarif Yönetim Sistemi

Bu proje, masaüstü ortamda çalışan bir **tarif yönetimi uygulamasıdır**. Kullanıcılar yeni yemek tarifleri ekleyebilir, tariflerde kullanılan malzemeleri tanımlayabilir, mevcut tarifleri güncelleyebilir ve silebilir. Ayrıca tarifleri malzemeye göre arayabilir ve eşleşme yüzdesine göre filtreleyebilirler.

## Özellikler

- Yeni tarif ekleme (malzeme adı, miktarı, birim fiyatı ile birlikte)
- Tarif silme ve güncelleme
- Malzeme bazlı arama ve eşleşme yüzdesine göre sıralama
- Tüm tarifleri listeleme
- Aynı tarifin birden fazla eklenmesini engelleme
- Mevcut malzemeleri kontrol edip veri tekrarını önleme
- Modern Swing arayüz tasarımı (NetBeans GUI Designer ile)

## Kullanılan Teknolojiler

- **Programlama Dili:** Java  
- **GUI:** Java Swing 
- **Veritabanı:** Azure Data Studio

## Veritabanı Yapısı

- `Tarifler`: Tarif adı, hazırlanma süresi, maliyet
- `Malzemeler`: Malzeme adı, birim, fiyat
- `TarifMalzemeIliskisi`: Tarif-Malzeme bağlantısı ve miktar bilgisi

