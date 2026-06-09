function toggleAll(btn) {
   var sec = btn.closest('.section');
   var details = sec.querySelectorAll('details');
   var expand = btn.textContent === 'Expand All';
   details.forEach(function(d) { d.open = expand; });
   btn.textContent = expand ? 'Collapse All' : 'Expand All';
}
