#set page(paper: "a4", flipped: true)
#set text(size: 10pt)

= List of Securities and Book Entry Shares (Form 8A)

#table(
  columns: (3fr, 1fr, 1fr, 2fr, 2fr, 2fr),
  align: (left, left, right, right, right, right),
  table.header(
    [*Name of company or cooperative*],
    [*Business ID*],
    [*Share quantity*],
    [*Undepreciated acquisition cost for purposes of income tax*],
    [*Comparison value per unit*],
    [*Comparison value in total*],
  ),
  {rows}
)
